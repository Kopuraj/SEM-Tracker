# Use Node.js LTS version
FROM node:20-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm ci --silent

# Copy the rest of your application files
COPY . .

# Expose the port your app runs on
EXPOSE 5173

# Define the command to run your app - USE THE CORRECT SCRIPT
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
