Feature: Pet store search for pets

  @e2e
  Scenario: Search for available pets
    When Bob searches for pets with status "available"
    Then Bob receives SC_OK response
    And Bob finds 213 pets with name "doggie" and status "available"

  @e2e @v1
  Scenario: Search for available pets api v1 (this is example how api with multiple versions could work )
    When Bob searches for pets with status "available"
    Then Bob receives SC_OK response
    And Bob finds 213 pets with name "doggie" and status "available"

  @stubbed
  Scenario: Search for available pets against stubbed service
    When Bob searches for pets with status "available"
    Then Bob receives SC_OK response
    And Bob finds 10 pets with name "doggie" and status "available"

  @e2e
  Scenario: Search for pets with invalid status value
    When Bob searches for pets with status "nonexisting"
    Then Bob receives SC_BAD_REQUEST response