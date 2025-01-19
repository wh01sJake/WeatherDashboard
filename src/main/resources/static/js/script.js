/**
 * `script.js`
 * Handles weather dashboard functionality: Searching weather, working with favorites, and session handling.
 */

// Backend API URL (adjust this if needed)
const BASE_API_URL = window.location.origin + "/api";

// DOM Elements
const searchButton = document.getElementById("search");
const cityNameInput = document.getElementById("cityName");
const weatherContent = document.getElementById("weather-content");
const forecastCards = document.getElementById("forecast-cards");
const favoritesList = document.getElementById("favoriteCitiesList");
const addFavoriteBtn = document.getElementById("addFavoriteBtn");

// Favorites (stored locally for now)
const favorites = JSON.parse(localStorage.getItem("favorites")) || [];

/**
 * Ensures the user is logged in before any API call or interaction.
 * Redirects to login page if the user is not logged in.
 */
function ensureLoggedIn() {
    const token = sessionStorage.getItem("token");
    if (!token) {
        alert("You must be logged in to perform this action.");
        window.location.href = "login.html"; // Redirect to login page
        return false;
    }
    return true;
}

/**
 * Handles the "Search" button click.
 */
async function handleSearch() {
    // Ensure the user is logged in
    if (!ensureLoggedIn()) return;

    const city = cityNameInput.value.trim();
    if (!city) {
        alert("Please enter a city name!");
        return;
    }

    try {
        // Show a loading message/spinner while the data is being fetched
        weatherContent.innerHTML = `<p>Loading weather data...</p>`;
        forecastCards.innerHTML = `<p>Loading forecast...</p>`;

        // Fetch weather and forecast data
        const weatherData = await apiFetch(`/weather?city=${encodeURIComponent(city)}`);
        const forecastData = await apiFetch(`/weather/forecast?city=${encodeURIComponent(city)}`);

        // Update the UI with weather and forecast data
        displayCurrentWeather(weatherData);
        displayForecast(forecastData);

        // Clear input field
        cityNameInput.value = "";
    } catch (error) {
        console.error("Error fetching weather data:", error);

        // Handle unauthorized errors explicitly
        if (error.message.includes("401")) {
            alert("Your session has expired. Please log in again.");
            window.location.href = "login.html"; // Redirect to login
        } else {
            alert("An unexpected error occurred. Please try again later.");
        }
    }
}

/**
 * Handles adding a city to favorites.
 */
function handleAddToFavorites() {
    // Check if user is logged in
    if (!ensureLoggedIn()) return;

    const city = cityNameInput.value.trim();
    if (!city) {
        alert("Please search for a city first before adding to favorites.");
        return;
    }

    // Check for duplicates (case-insensitive)
    const normalizedCity = city.toLowerCase();
    const isDuplicate = favorites.some(f => f.toLowerCase() === normalizedCity);
    if (isDuplicate) {
        alert("City is already in favorites!");
        return;
    }

    // Add city to favorites
    favorites.push(city);
    localStorage.setItem("favorites", JSON.stringify(favorites));
    displayFavorites();
}

/**
 * Display the current weather in the UI.
 * 
 * @param {object} data - Weather data from backend response.
 */
function displayCurrentWeather(data) {
    const tempCelsius = data.temperature;
    const description = data.weather;
    const iconUrl = `https://openweathermap.org/img/wn/${data.icon}@2x.png`;

    weatherContent.innerHTML = `
        <h3>${data.city}, ${data.country}</h3>
        <img src="${iconUrl}" alt="${description}">
        <p><strong>Temperature:</strong> ${tempCelsius}°C</p>
        <p><strong>Description:</strong> ${description}</p>
        <p><strong>Humidity:</strong> ${data.humidity}%</p>
    `;
}

/**
 * Display the 5-day weather forecast in the UI.
 * 
 * @param {Array} data - Forecast data from backend response.
 */
function displayForecast(data) {
    forecastCards.innerHTML = ""; // Clear existing cards

    data.forEach(forecast => {
        const date = new Date(forecast.date).toLocaleDateString();
        const tempCelsius = forecast.temperature;
        const iconUrl = `https://openweathermap.org/img/wn/${forecast.icon}@2x.png`;

        forecastCards.innerHTML += `
        <div class="col-md-4">
            <div class="card mb-3" style="max-width: 18rem;">
                <div class="card-header">${date}</div>
                <div class="card-body text-center">
                    <img src="${iconUrl}" alt="${forecast.weather}" class="mb-2">
                    <p><strong>${tempCelsius}°C</strong></p>
                    <p>${forecast.weather}</p>
                </div>
            </div>
        </div>
        `;
    });
}

/**
 * Displays the list of favorite cities in the UI.
 */
function displayFavorites() {
    favoritesList.innerHTML = ""; // Clear old items

    favorites.forEach(city => {
        const listItem = document.createElement("li");
        listItem.className = "list-group-item d-flex justify-content-between align-items-center";
        listItem.textContent = city;

        const removeBtn = document.createElement("button");
        removeBtn.className = "btn btn-danger btn-sm";
        removeBtn.textContent = "Remove";
        removeBtn.addEventListener("click", () => {
            removeFromFavorites(city);
        });

        listItem.appendChild(removeBtn);
        favoritesList.appendChild(listItem);
    });
}

/**
 * Removes a city from favorites.
 */
function removeFromFavorites(city) {
    const index = favorites.indexOf(city);
    if (index !== -1) {
        favorites.splice(index, 1);
        localStorage.setItem("favorites", JSON.stringify(favorites));
        displayFavorites();
    }
}

/**
 * Utility function to make API calls.
 * 
 * @param {string} endpoint - Backend API endpoint.
 * @returns {Promise<object>} - Parsed JSON response.
 */
async function apiFetch(endpoint) {
    const token = sessionStorage.getItem("token");
    const headers = { "Content-Type": "application/json" };

    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(`${BASE_API_URL}${endpoint}`, { headers });
    if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);

    return response.json();
}

// Attach event listeners
searchButton.addEventListener("click", handleSearch);
addFavoriteBtn.addEventListener("click", handleAddToFavorites);
document.addEventListener("DOMContentLoaded", displayFavorites);