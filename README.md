# 🛒 Super Market Product Management System

A Java Swing + MySQL based Product Management System with 
Category, Sub-Category and Product hierarchy.

## ✨ Features
- 📁 Category Management (Add & View)
- 📂 Sub-Category Management (Add & View)
- 📦 Product Management (Add, Update, Delete, Search)
- 🔍 Real-time Product Search
- 🖱️ Click-based Navigation (Category → SubCategory → Product)
- 📋 Dynamic Table Loading from Database

## 🛠️ Technologies Used
- Java (Swing + AWT)
- MySQL Database
- JDBC Connectivity

## 🗄️ Database Tables

| Table | Description |
|-------|-------------|
| `categories` | Stores product categories |
| `sub_categories` | Stores sub-categories linked to categories |
| `products` | Stores products linked to sub-categories |

## 🗃️ Database Setup

```sql
CREATE DATABASE keerthana_database;
USE keerthana_database;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE sub_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100),
    price DOUBLE,
    sub_category_id INT,
    quantity INT,
    description VARCHAR(255),
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id)
);
```

## ▶️ How to Run
1. Install Java JDK 17+
2. Install MySQL
3. Run the SQL setup above
4. Add MySQL connector JAR to classpath
5. Run `ProductManagementSystem.java`

## 👩‍💻 Developer
Keerthana
