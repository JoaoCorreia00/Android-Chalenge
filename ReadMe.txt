Development Strategy:

My approach was to build the foundation of the application first and then refine it, aiming to meet all requirements within the 7-day limit.

1.Create all the necessary screens and UI elements

2.Make sure there are no bugs or logic errors in the core features

3.Implement data persistence for favourites using Room

4.Refactor the code to follow MVVM architecture

5.Add basic unit test coverage

6.Prioritise required features, and add bonus points only if time allowed



Days 1–2: First Steps and Core UI:
	- This was my first time working with Kotlin and Android Studio, so I focused on learning the basics.

	- Followed a basic Compose tutorial to understand UI structure.

	- Set up the initial navigation bar with icons and created a basic layout displaying cat images from the API.

	- Integrated Room database into the project to store favourites.

	- Made the favourite button functional, allowing users to add/remove favourites in local storage.

Days 3–4: Breed List and Detail Features:
	- Created the Favourites screen, showing cats saved in the database.

	- Fetched the full list of breeds using The Cat API and displayed them in a scrollable list.

	- Implemented the breed detail screen by fetching breed details by ID.

	- Added an additional endpoint to retrieve images by breed.

	- Built the logic to display full breed details including name, origin, temperament, and description.

Days 5–6: Search and MVVM Architecture:
	- Added a search bar in the breed list screen with suggestions and quick access buttons.

	- Refactored the project structure to follow MVVM architecture more strictly:

Day 7: Testing & Final Touches:
	-Wrote unit tests for the ViewModel, reaching ~60% coverage.