# Vérifier si PowerShell est exécuté en mode administrateur
if (-not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "Ce script nécessite des droits administrateur. Relance du script en mode administrateur..."

    # Relancer le script avec les privilèges administratifs
    $args = [System.Environment]::GetCommandLineArgs()
    Start-Process powershell -ArgumentList "Start-Process powershell -ArgumentList '$args' -Verb runAs" -NoNewWindow
    exit
}

# Configuration de l'exécutable socat
$socatPath = "C:\Applications\socat\socat.exe"

# Vérifier si l'exécutable existe
if (-not (Test-Path $socatPath)) {
    Write-Host "Erreur : Socat n'est pas installé ou le chemin est incorrect : $socatPath"
    exit 1
}

# Obtenir l'IP de WSL dynamiquement
$ip = wsl -- ip -o -4 -json addr list eth0 | ConvertFrom-Json | ForEach-Object { $_.addr_info.local } | Where-Object { $_ }

# Vérifier si une adresse IP a été récupérée
if (-not $ip) {
    Write-Host "Aucune adresse IP trouvée pour WSL."
    exit 1
}

# Afficher l'IP de WSL
Write-Host "L'IP de WSL est : $ip"

# Port de la cible dans WSL
$targetPort = 80   # Le port cible du service dans WSL
$localPort = 8080   # Le port local à utiliser (ici 8080)

# Lancer Socat avec redirection
Write-Host "Lancement de socat : rediriger 127.0.0.1:$localPort vers $($ip):$targetPort"

# Lancer Socat dans la même fenêtre PowerShell
Start-Process -FilePath $socatPath -ArgumentList "TCP-LISTEN:$localPort,fork,bind=127.0.0.1 TCP:$($ip):$targetPort" -NoNewWindow -Wait

Write-Host "Socat a été lancé avec succès. Le port $localPort est redirigé vers $($ip):$targetPort."
Write-Host "Appuyez sur une touche pour fermer cette fenêtre..."
[System.Console]::ReadKey() | Out-Null
