This app is a sample project built using compose that does the following:

1. Request permission to access the device location
2. The app prepopulates the Search field with the appropriate city name if permission is granted.
3. If the location isn't available locally, fetch the latitude and longitude info from the API and use it to fetch the weather info.
4. Fetch the Weather info from the Weather API and display it to the user.
5. Handles loading and error states and displays the bare minimum UI to convey the state updates to the user.

*** In order to fetch the weather info from the API, API_KEY is necessary. Since this repo is public the key isn't added to the project. Hence, an API_KEY must be added to your local.properties file to get the weather info from the API. You need to add the key to the properties file in the following format.
WEATHER_API_KEY="<YOUR_KEY_HERE>"
