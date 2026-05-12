package istio.authz

test_deny_srozange_to_customer_service if {
    not allow with input as {
        "attributes": {"request": {"http": {
            "headers": {"authorization": "Basic c3JvemFuZ2U6dG90bw=="},
            "host": "bank-customer-service:8080"
        }}}
    }
}

test_allow_other_user if {
    allow with input as {
        "attributes": {"request": {"http": {
            "headers": {"authorization": "Basic b3RoZXI6cGFzcw=="},
            "host": "bank-customer-service:8080"
        }}}
    }
}

test_allow_srozange_to_account_service if {
    allow with input as {
        "attributes": {"request": {"http": {
            "headers": {"authorization": "Basic c3JvemFuZ2U6dG90bw=="},
            "host": "bank-account-service:8080"
        }}}
    }
}