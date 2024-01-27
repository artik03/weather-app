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

  selectUrlChange();
  handleShowPasswordIcons();
  adjustSearch();
  window.addEventListener("resize", adjustSearch);
};

window.addEventListener("load", app);
