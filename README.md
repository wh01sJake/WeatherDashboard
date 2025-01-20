# Weather Dashboard

## 🌟 Overview

The **Weather Dashboard** is a Java-based application developed using **Jakarta EE**, **Spring Framework**, and other modern technologies to fetch and display dynamic weather information. This project provides real-time weather updates for any city, offers an intuitive user interface, and demonstrates modular software development practices.


---

## 🌐 Live Demo

The application is currently deployed on **Heroku**. Check it out here:

👉 **[Weather Dashboard Live](https://my-weatherdashboard-app-0957e4d7c137.herokuapp.com)**

Feel free to explore the project and view real-time weather updates for various cities!

---
This project showcases:
- **Software engineering principles** suitable for modern cloud-based applications.
- **API integration** to fetch real-time data.
- A practical implementation of **Spring MVC**, **Spring Data JPA**, and **Spring Boot**.
- Complete focus on **testable and maintainable architecture** with secure coding principles.

---

## 🎯 Key Features
- **Search for Weather**: Enter a city name to fetch real-time weather updates.
- **Current Conditions**: Displays temperature, humidity, wind speed, and more.
- **5-Day Forecast**: View a detailed 7-day weather forecast at a glance.
- **Responsive Design**: Works seamlessly on desktop and mobile devices.
- **Error Handling**: Graceful handling of network or API issues with user-friendly messages.
- **Technology Showcase**: Built using modern web development tools and frameworks.

---

## 🛠️ Technologies Used

- **Language**: Java 21  
- **Frameworks**:  
  - Jakarta EE  
  - Spring Boot  
  - Spring MVC  
  - Spring Data JPA
- **API**: Integrated with OpenWeatherMap (or similar weather API)
- **Database**: MySQL (or any database supported by JPA)
- **UI Layer**: HTML5, CSS, JavaScript, Bootstrap (for styling and responsiveness)
- **Tools**:
  - IntelliJ IDEA 2024.2.2 Ultimate Edition (development environment)
  - Maven (dependency management)
  - Git/GitHub (for version control)

---

## 🚀 Getting Started

Follow these steps to setup the **Weather Dashboard** project on your local machine.

### 1. Prerequisites

Ensure you have the following installed:
- **Java 21 JDK**
- **Maven** (build tool)
- **MySQL** (if used for relational database)
- **Git** (for version control)

### 2. Clone the Repository
```bash
git clone https://github.com/your-username/WeatherDashboard.git
cd WeatherDashboard
```

### 3. Configure the Environment

1. Obtain your API key from [OpenWeatherMap](https://openweathermap.org/api).
2. Set up the API key and base URL in a configuration file:
   - Open `application.yml` and edit:
     ```yaml
     weather:
       api-key: YOUR_API_KEY
       base-url: https://api.openweathermap.org
     ```

3. Configure database settings in the `application.yml` file:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/weather_db
       username: your-database-username
       password: your-database-password
     jpa:
       hibernate.ddl-auto: update
   ```

### 4. Build and Run the Application

Use Maven to build and run the project:
```bash
mvn spring-boot:run
```

- The application should now be accessible at `http://localhost:8080`.

---

## 📸 Screenshots

![Weather Dashboard Screenshot](https://github.com/user-attachments/assets/be027c06-5b7c-416e-b026-75ab6aa7b090)
*Home page*

![Weather Dashboard Screenshot](https://github.com/user-attachments/assets/cca09f8f-4378-4fa3-b3b7-6c39afffd688)
*Login*

![Weather Dashboard Screenshot](https://github.com/user-attachments/assets/e9f2930e-609e-452d-a874-694ae24cc307)
*Register*

![Weather Dashboard Screenshot](https://github.com/user-attachments/assets/fd7392bd-77db-4f7b-a444-815f9232ff02)
*Real-time weather search interface*


---


## 🛡️ Security

The project follows industry best practices:
- **Secrets Management**: API keys and sensitive credentials are stored securely using environment variables and configurations.
- **Database Security**: No hardcoding of database credentials.

---

## 🗺️ Future Enhancements

The project has opportunities for future growth:
- Add multi-language support for the UI.
- Visual weather graphics (e.g., charts and graphs).
- Analytics on weather trends over time.
- Implement caching mechanisms to reduce API requests.

---



---

## 🤝 Contributing

Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature-branch-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Describe your changes"
   ```
4. Push to your fork:
   ```bash
   git push origin feature-branch-name
   ```
5. Submit a pull request.

---

## 📜 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

---

## ❤️ Acknowledgments

- **OpenWeatherMap**: Weather API provider used in the project.
- **Spring Community**: For building robust Java frameworks that made this project possible.
- **Jakarta EE**: For providing enterprise-level tools for developers.

---
