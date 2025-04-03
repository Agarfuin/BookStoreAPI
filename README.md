# Agarfuin's BookStore API

<p align="center">
  <a href="https://spring.io/projects/spring-boot" target="_blank" rel="noopener noreferrer">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4.1-brightgreen.svg" alt="Spring Boot">
  </a>
      <a href="https://www.java.com" target="_blank" rel="noopener noreferrer">
    <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java">
  </a>
  <a href="https://www.opensource.org./licenses/MIT" target="_blank" rel="noopener noreferrer">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License">
  </a>
</p>

The **BookStore API** is a **fast, secure, scalable, and maintainable** backend application designed to efficiently
handle book-related operations. Built using a **microservices architecture**, this API ensures modularity, ease of
maintenance, and seamless scalability to meet growing demands.

## Key Features

- **High Performance:** Optimized for speed and efficiency.
- **Security:** Implements JWT based authentication and authorization mechanisms to protect user data.
- **Scalability:** Designed to handle increasing loads with independent, modular services.
- **Maintainability:** Well-structured codebase following industry best practices.
- **Event-Driven Communication:** Utilizes **Apache Kafka** as a message queue to handle asynchronous tasks, such as
  sending email notifications.

This API serves as a reliable foundation for building modern e-commerce platforms, digital bookstores, or any book
management system requiring a robust backend.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
        - [Docker Compose](#docker-compose)
        - [Kubernetes Setup](#kubernetes-setup)
- [Usage Guide](#usage-guide)
- [API Documentation](#api-documentation)
    - [Authentication](#authentication)
    - [Books](#books)
    - [Users](#users)
        - [Shopping Cart](#shopping-cart)
- [License](#license)

## Technology Stack

- **Backend Framework**: Spring Boot 3.4.1
- **Language**: Java 17
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Message Queue**: Apache Kafka
- **Service Discovery**: Eureka
- **Request Tracing**: Zipkin
- **Containerization**: Docker
- **Container Orchestration**: Docker Compose / Kubernetes
- **Build Tool**: Apache Maven
- **API Testing**: Postman collection provided

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9.9 or higher
- Docker
- Kubernetes (optional)

### Installation

**IMPORTANT**: Before starting, make sure that you put your PostgreSQL, Mail Provider credentials, and JWT secret key in
the docker-compose.yml or /k8s/secrets/secrets.yml files. You can find the related fields by searching for
`"YOUR"` keyword in those files.

1. Clone the repository
   ```bash
   git clone https://github.com/Agarfuin/BookStoreAPI.git
   ```

2. Navigate to the project directory
   ```bash
   cd BookStoreAPI
   ```

3. Build the project
   ```bash
   ./mvnw clean install
   ```

#### Docker Compose

From this point, you can choose to run the project using Docker or Kubernetes.

4. Run docker compose
   ```bash
   docker-compose up -d
   ```

The API will be available at `http://localhost:8080`

#### Kubernetes Setup

If you want to run the project without docker compose, you can continue from the third step below:

4. Start Minikube
   ```bash
   minikube start
   ``` 

5. Deploy the application
   ```bash
   kubectl apply -f k8s/
   ```

6. Get the external IP of the Api-Gateway
   ```bash
   kubectl get svc api-gateway
   ```

The API will be available at `http://<external-ip>:8080`

## Usage Guide

1. **Registration**: Create a new account.
2. **Verification**: Verify your account via the link sent to your account's email.
3. **Authentication**: Log in to receive your JWT token.
4. **Explore Books**: Use the token to explore books in the database.
5. **Place Orders**: Add the desired books into your cart and checkout.
6. **Generate Admin Token**: Generate admin token to access admin features such as:
    1. Create/Update/Delete books
    2. See all carts and orders
    3. See all users
    4. And many more...

A comprehensive Postman collection is added (`BookStoreAPI.postman_collection.json`) for you to explore all API
endpoints.

## API Documentation

### Authentication

#### Signup

- **URL**: `/api/v1/auth/signup`
- **Method**: `POST`
- **Description**: Create a new user account and return a UUID token for verification
- **Request Body**:
  ```json
  {
    "firstName": "Harun Selman",
    "lastName": "Karakas",
    "email": "harun_selman_karakas@hotmail.com",
    "password": "test123"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "token": "4931cad7-7da9-4b7d-a133-3bbd44da957f"
  }
  ```

After signing-up, you will see a new mail in your inbox to verify your account:

<p align="center">
  <img src="./notification/src/main/resources/img/SampleEmailNotification.png" width="75%" alt="Sample Email Notification">
</p>

#### Verify Account

- **URL**: `/api/v1/auth/verify`
- **Method**: `GET`
- **Description**: Verify a user account using the token received during signup
- **Query Parameters**:
    - `token`: Required, verification token
- **Response**: `200 OK`
  ```
    Email verified!
  ```

#### Login

- **URL**: `/api/v1/auth/login`
- **Method**: `POST`
- **Description**: Authenticate a user and return a JWT token
- **Request Body**:
  ```json
  {
    "email": "harun_selman_karakas@hotmail.com",
    "password": "test123"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoYXJ1bl9zZWxtYW5fa2FyYWthc0Bob3RtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzQzNjk1MzM2LCJleHAiOjE3NDM3ODE3MzZ9.EnifDvTZzIhzhvWTyED47tLaNj8vAAu8pHqx4jPxgQWQm8qCwMOGfXZ7ytoQLTmHJkoezb64NBz2E-nFSNBebw"
  }
  ```

#### Generate Admin Token

- **URL**: `/api/v1/auth/generate-admin-token`
- **Method**: `GET`
- **Description**: Generate an admin token for admin operations
- **Response**: `200 OK`
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZ2FyZnVpbmJvb2tzdG9yZUBtYWlsLmNvbSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc0MzY5NTUyMywiZXhwIjoxNzQzNzgxOTIzfQ.zMHMTENSyDxG0KN2HJT93Ohxe4yWjYzF2fLhwac4KnX_iMju_ahi5Mf-ooONb3KdvX0KctEQd-cUiwjO7hz2IA"
  }
  ```

### Books

#### Add Book

- **URL**: `/api/v1/admin/books`
- **Method**: `POST`
- **Description**: Add a new book to the database
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Request Body**:
  ```json
  {
    "title": "Test Book1",
    "author": "Test Author1",
    "description": "Test Description1",
    "publicationYear": 2025,
    "genre": ["CLASSIC", "BIOGRAPHY"],
    "price": 1.23,
    "quantity": 10
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "bookId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20"
  }
  ```

#### Update Book By ID

- **URL**: `/api/v1/admin/books/{bookId}`
- **Method**: `PATCH`
- **Description**: Update an existing book in the database
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Request Body**:
  ```json
  {
    "title": "Update Book1",
    "author": "Update Author1",
    "description": "Update Description1",
    "publicationYear": 2024,
    "price": 4.56,
    "quantity": 99
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "bookId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
    "title": "Update Book1",
    "author": "Update Author1",
    "description": "Update Description1",
    "publicationYear": 2024,
    "genre": [
        "CLASSIC",
        "BIOGRAPHY"
    ],
    "price": 4.56,
    "quantityInStock": 99
  }
  ```

#### Get Book By ID

- **URL**: `/api/v1/admin/books/{bookId}`
- **Method**: `GET`
- **Description**: Return an existing book in the database
- **Request Headers**:
    - `Authorization`: Required, Bearer user/admin token
- **Response**: `200 OK`
  ```json
  {
    "bookId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
    "title": "Update Book1",
    "author": "Update Author1",
    "description": "Update Description1",
    "publicationYear": 2024,
    "genre": [
        "CLASSIC",
        "BIOGRAPHY"
    ],
    "price": 4.56,
    "quantityInStock": 99
  }
  ```

#### Get All Books

- **URL**: `/api/v1/admin/books`
- **Method**: `GET`
- **Description**: Return all books in the database
- **Request Headers**:
    - `Authorization`: Required, Bearer user/admin token
- **Response**: `200 OK`
  ```json
  [
    {
        "bookId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
        "title": "Update Book1",
        "author": "Update Author1",
        "description": "Update Description1",
        "publicationYear": 2024,
        "genre": [
            "CLASSIC",
            "BIOGRAPHY"
        ],
        "price": 4.56,
        "quantityInStock": 99
    },
    {
        "bookId": "84a9b884-8a06-45c6-bda7-2ffadc3a1ee9",
        "title": "Test Book2",
        "author": "Test Author2",
        "description": "Test Description2",
        "publicationYear": 2000,
        "genre": [
            "ADVENTURE"
        ],
        "price": 9.99,
        "quantityInStock": 50
    }
  ]
  ```

#### Delete Book By ID

- **URL**: `/api/v1/admin/books/{bookId}`
- **Method**: `DELETE`
- **Description**: Delete an existing book from the database
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```
  Book successfully deleted from the store!
  ```

### Users

#### Get User Details

- **URL**: `/api/v1/admin/users/user`
- **Method**: `GET`
- **Description**: Return current user's details
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Response**: `200 OK`
  ```json
  {
    "id": "b1f56f94-08cb-4562-bd3a-af74005a1af5",
    "email": "harun_selman_karakas@hotmail.com",
    "role": "USER",
    "firstName": "Harun Selman",
    "lastName": "Karakas",
    "isVerified": true,
    "accountCreationDate": "2025-04-03T15:42:48.847512Z"
  }
  ```

#### Delete Account

- **URL**: `/api/v1/admin/users/user`
- **Method**: `DELETE`
- **Description**: Delete current user from the database
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Response**: `200 OK`
  ```
  User with id b1f56f94-08cb-4562-bd3a-af74005a1af5 deleted
  ```

#### Get All Users

- **URL**: `/api/v1/admin/users`
- **Method**: `GET`
- **Description**: Return all users from the database
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```json
  [
    {
        "id": "3c70a1b3-a857-4e77-ba59-e7e7c4c93dcd",
        "email": "test1@mail.com",
        "role": "USER",
        "firstName": "testFirstName1",
        "lastName": "testLastName1",
        "isVerified": true,
        "accountCreationDate": "2025-04-03T16:25:42.162382Z"
    },
    {
        "id": "e9dd217f-affb-4c56-a397-15855eb2540a",
        "email": "test2@mail.com",
        "role": "USER",
        "firstName": "testFirstName2",
        "lastName": "testLastName2",
        "isVerified": false,
        "accountCreationDate": "2025-04-03T16:26:05.090398Z"
    },
    {
        "id": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "email": "harun_selman_karakas@hotmail.com",
        "role": "USER",
        "firstName": "Harun Selman",
        "lastName": "Karakas",
        "isVerified": true,
        "accountCreationDate": "2025-04-03T16:28:19.925333Z"
    }
  ]
  ```

#### Get User By ID

- **URL**: `/api/v1/admin/users/user/{userId}`
- **Method**: `GET`
- **Description**: Return an existing user's details
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```json
  {
    "id": "3c70a1b3-a857-4e77-ba59-e7e7c4c93dcd",
    "email": "test1@mail.com",
    "role": "USER",
    "firstName": "testFirstName1",
    "lastName": "testLastName1",
    "isVerified": true,
    "accountCreationDate": "2025-04-03T16:25:42.162382Z"
  }
  ```

#### Shopping Cart

##### Add Item To Cart

- **URL**: `/api/v1/users/cart`
- **Method**: `POST`
- **Description**: Add an existing item to current user's cart
- **Request Headers**:
    - `Authorization`: Required, Bearer user/admin token
- **Request Body**:
  ```json
  {
    "itemId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
    "quantity": 5
  }
  ```
- **Response**: `201 CREATED`
  ```json
  {
    "cartId": 1
  }
  ```

##### Get Cart Details

- **URL**: `/api/v1/users/cart`
- **Method**: `GET`
- **Description**: Return current user's cart details
- **Request Headers**:
    - `Authorization`: Required, Bearer user/admin token
- **Response**: `200 OK`
  ```json
  {
    "cartId": 1,
    "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
    "cartItems": [
        {
            "id": 1,
            "itemId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
            "title": "Update Book1",
            "quantity": 5,
            "pricePerUnit": 4.56,
            "overallPrice": 22.80
        }
    ],
    "totalPrice": 22.80,
    "status": "PENDING"
  }
  ```

##### Update Item In Cart

- **URL**: `/api/v1/users/cart/{itemId}`
- **Method**: `PATCH`
- **Description**: Update an existing item in current user's cart
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Query Parameters**:
    - `quantity`: Required, new quantity of the item
- **Response**: `200 OK`
  ```
  Updated item with ID: 4d9f12ff-6f2b-4f26-a059-39a3d041bf20
  ```

##### Remove Item From Cart

- **URL**: `/api/v1/users/cart/{itemId}`
- **Method**: `DELETE`
- **Description**: Delete an existing item in current user's cart
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Response**: `200 OK`
  ```
  Removed item with ID: 4d9f12ff-6f2b-4f26-a059-39a3d041bf20
  ```

##### Clear Cart

- **URL**: `/api/v1/users/cart`
- **Method**: `DELETE`
- **Description**: Remove all items from current user's cart
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Response**: `200 OK`
  ```
  Cart successfully cleared
  ```

##### Checkout

- **URL**: `/api/v1/users/cart/checkout`
- **Method**: `POST`
- **Description**: Checkout current user's cart
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Query Parameters**:
    - `address`: Required, delivery address
    - `paymentMethod`: Required, payment method `(CREDIT_CARD, CASH, PAYPAL, CRYPTO)`
- **Response**: `200 OK`
  ```json
  {
    "checkoutDate": "2025-04-03T19:50:55.049031",
    "cartId": 1,
    "address": "testAddress",
    "totalPrice": 22.80,
    "paymentMethod": "CREDIT_CARD"
  }
  ```

##### Get Cart History

- **URL**: `/api/v1/users/cart/history`
- **Method**: `GET`
- **Description**: Get current user's cart history
- **Request Headers**:
    - `Authorization`: Required, Bearer user token
- **Response**: `200 OK`
  ```json
  [
    {
        "cartId": 1,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [
            {
                "id": 2,
                "itemId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
                "title": "Update Book1",
                "quantity": 5,
                "pricePerUnit": 4.56,
                "overallPrice": 22.80
            }
        ],
        "totalPrice": 22.80,
        "status": "CHECKED_OUT"
    },
    {
        "cartId": 2,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [],
        "totalPrice": 0,
        "status": "PENDING"
    }
  ]
  ```

##### Get Cart By ID

- **URL**: `/api/v1/admin/users/cart/{cartId}`
- **Method**: `GET`
- **Description**: Return an existing user's cart details
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```json
  {
    "cartId": 2,
    "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
    "cartItems": [],
    "totalPrice": 0,
    "status": "PENDING"
  }
  ```

##### Get All Carts

- **URL**: `/api/v1/admin/users/cart`
- **Method**: `GET`
- **Description**: Return all user's cart details
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```json
  [
    {
        "cartId": 1,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [
            {
                "id": 2,
                "itemId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
                "title": "Update Book1",
                "quantity": 5,
                "pricePerUnit": 4.56,
                "overallPrice": 22.80
            }
        ],
        "totalPrice": 22.80,
        "status": "CHECKED_OUT"
    },
    {
        "cartId": 2,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [],
        "totalPrice": 0,
        "status": "PENDING"
    }
  ]
  ```

##### Get All Purchase History

- **URL**: `/api/v1/admin/users/cart/history`
- **Method**: `GET`
- **Description**: Return all user's cart histories
- **Request Headers**:
    - `Authorization`: Required, Bearer admin token
- **Response**: `200 OK`
  ```json
  [
    {
        "cartId": 1,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [
            {
                "id": 2,
                "itemId": "4d9f12ff-6f2b-4f26-a059-39a3d041bf20",
                "title": "Update Book1",
                "quantity": 5,
                "pricePerUnit": 4.56,
                "overallPrice": 22.80
            }
        ],
        "totalPrice": 22.80,
        "status": "CHECKED_OUT"
    },
    {
        "cartId": 2,
        "userId": "2024acea-5c7a-4da4-b0d8-aa72a1162b99",
        "cartItems": [],
        "totalPrice": 0,
        "status": "PENDING"
    },
    {
        "cartId": 3,
        "userId": "3c70a1b3-a857-4e77-ba59-e7e7c4c93dcd",
        "cartItems": [],
        "totalPrice": 0,
        "status": "PENDING"
    }
  ]
  ```

## License

This project is licensed under the MIT License - see [here](https://opensource.org/license/mit) for details.
