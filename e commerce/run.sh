#!/bin/bash

echo "Compiling E-Commerce application..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Starting server on http://localhost:8080"
echo ""
mvn exec:java -Dexec.mainClass="com.ecommerce.api.ECommerceServer"

