#!/usr/bin/env python3
"""
Script to analyze OWASP Dependency Check CSV reports and extract security metrics.
"""
import csv
import sys
from collections import defaultdict

def analyze_cve_report(csv_file):
    """Analyze CVE report and extract metrics by severity."""
    cve_count = defaultdict(int)
    dep_count = defaultdict(int)
    unique_deps = set()
    total_cves = 0

    try:
        with open(csv_file, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                severity = row.get('CVSSv3_BaseSeverity', '').strip().upper()
                if not severity:
                    severity = row.get('CVSSv4_BaseSeverity', '').strip().upper()

                cve = row.get('CVE', '').strip()
                dep_name = row.get('DependencyName', '').strip()

                if cve and severity:
                    cve_count[severity] += 1
                    total_cves += 1

                    dep_key = f"{dep_name}:{severity}"
                    if dep_key not in unique_deps:
                        unique_deps.add(dep_key)
                        dep_count[severity] += 1

        # Print results
        print(f"Total CVEs: {total_cves}")
        print(f"\nCVE Count by Severity:")
        for severity in ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW']:
            count = cve_count.get(severity, 0)
            print(f"  {severity}: {count}")

        print(f"\nVulnerable Dependencies by Severity:")
        for severity in ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW']:
            count = dep_count.get(severity, 0)
            print(f"  {severity}: {count}")

        return {
            'total_cves': total_cves,
            'cve_critical': cve_count.get('CRITICAL', 0),
            'cve_high': cve_count.get('HIGH', 0),
            'cve_medium': cve_count.get('MEDIUM', 0),
            'cve_low': cve_count.get('LOW', 0),
            'dep_critical': dep_count.get('CRITICAL', 0),
            'dep_high': dep_count.get('HIGH', 0),
            'dep_medium': dep_count.get('MEDIUM', 0),
            'dep_low': dep_count.get('LOW', 0),
            'total_vulnerable_deps': len(set(d.split(':')[0] for d in unique_deps))
        }

    except FileNotFoundError:
        print(f"Error: File {csv_file} not found")
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python3 analyze_cve.py <csv_file>")
        sys.exit(1)

    analyze_cve_report(sys.argv[1])
