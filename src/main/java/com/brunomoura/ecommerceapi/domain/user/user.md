# User

## Attributes
- id
- name
- cpf
- email
- phoneNumber
- dateOfBirth
- passwordHash
- role
- active
- addresses
- createdAt
- updatedAt

## Relationships
- User (1:N) Address
- User (1:1) Cart

## Rules
- id must be unique
- cpf must be unique
- email must be unique
- password cannot be null
- User controls Address lifecycle

## Decisions

- Address is a separate entity
- Role implemented as ENUM
- Table name: users