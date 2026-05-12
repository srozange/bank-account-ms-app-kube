package istio.authz

import rego.v1
import input.attributes.request.http as http_request

default allow = true

# Extraire le username
user_name = parsed if {
    [_, encoded] := split(http_request.headers.authorization, " ")
    [parsed, _] := split(base64url.decode(encoded), ":")
}

deny if {
    user_name == "srozange"
    startswith(http_request.host, "bank-customer-service")    
}

allow = false if {
    deny
}

# Debug
debug_info = {
    "user_name": user_name,
    "deny": deny,
    "allow": allow,
    "host": http_request.host
}
