const app = () => {
  const adjustSearch = () => {
    const windowWidth = window.innerWidth;

    const nav = document.querySelector("nav");
    const SearchForm = document.querySelector("#search-form");
    const formSmPlaceholder = document.querySelector(
      "#form-for-sm-display-placeholder"
    );

    if (SearchForm && SearchForm.parentNode) {
      /**
       * Create a copy of the SearchForm element
       * for some reason it throws error without it:
       * "Failed to execute 'appendChild' on 'Node':
       * parameter 1 is not of type 'Node'.""
       *  */
      const newSearchForm = SearchForm.cloneNode(true);

      SearchForm.parentNode.removeChild(SearchForm);

      // Append the new instance to the appropriate node
      if (windowWidth >= 700) {
        nav.appendChild(newSearchForm);
      } else {
        formSmPlaceholder.appendChild(newSearchForm);
      }
    }
  };

  const handleShowPasswordIcons = () => {
    inputGroups = document.querySelectorAll(".input-with-icon");

    if (inputGroups.length == 0) return;

    inputGroups.forEach((element) => {
      const span = element.querySelector("span");
      const input = element.querySelector("input");
      const eyeIcon = span.querySelector(".eye-icon");
      const eyeOffIcon = span.querySelector(".eye-off-icon");

      span.addEventListener("click", () => {
        //toggle input type
        if (input.type === "password") {
          input.type = "text";
          eyeIcon.style.display = "none";
          eyeOffIcon.style.display = "block";
        } else {
          input.type = "password";
          eyeIcon.style.display = "block";
          eyeOffIcon.style.display = "none";
        }
      });
    });
  };
  const selectUrlChange = () => {
    const select = document.querySelector("#city-select");

    if (!select) return;

    select.addEventListener("change", () => {
      window.location.href = `${window.location.pathname}?search=${select.value}`;
    });
  };

  const setMyWeatherButton = () => {
    if (window.location.pathname != "/current-weather/") return;

    const btn = document.querySelector("#my-weather");

    const getLocation = () => {
      if ("geolocation" in navigator) {
        // Prompt user for permission to access their location
        navigator.geolocation.getCurrentPosition(
          // Success callback function
          (position) => {
            // Get the user's latitude and longitude coordinates
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            fetch("/device-loc/", {
              method: "POST",
              body: JSON.stringify({
                lat: lat,
                lon: lon,
              }),
              headers: {
                "Content-type": "application/json; charset=UTF-8",
              },
            })
              .then((res) => {
                if (res.redirected) {
                  window.location.href = res.url;
                  console.log("redirected");
                }
              })
              .catch((e) => console.error(e));
          },
          // Error callback function
          (error) => {
            // Handle errors
            console.error("Error getting user location:", error);
            //alert("Error getting browser location. Getting location by IP.");
            window.location.href = `${window.location.pathname}?search=MY-LOCATION`;
          }
        );
      } else {
        // Geolocation is not supported by the browser
        //alert("Browser geolocation not allowed. Getting location by IP.");
        window.location.href = `${window.location.pathname}?search=MY-LOCATION`;
      }
    };

    btn.addEventListener("click", getLocation);
  };

  setMyWeatherButton();
  selectUrlChange();
  handleShowPasswordIcons();
  adjustSearch();

  window.addEventListener("resize", adjustSearch);
};

window.addEventListener("load", app);
