# Job Search

## Overview

The Job Search is a cross-platform Java-based desktop application developed to facilitate the organisation and analysis of job opportunities. The software enables users to manage job advertisements by adding, removing, sorting, and searching through listings, while also offering distance calculations from a user-defined home location and visualisations using scatter plots. The system is designed following object-oriented principles and common software design patterns.

This application is particularly suitable for students, educators, or professionals aiming to understand how modular Java GUI applications can be architected using principles such as Model-View-Controller (MVC) and structured data management.

## Key Features

- Add, remove, and edit job postings.
- Search jobs by title, company, or required skills.
- Sort job listings by various criteria such as salary, deadline, or title.
- Set a home location and calculate geographical distances to job sites.
- Visualise job locations and distances using a scatter plot (JFreeChart).
- Import and export job data via CSV files.
- Integrated Prometheus monitoring for runtime metrics and observability.

## System Requirements

- Java Development Kit (JDK) version 8 or above.
- Compatible with Windows and macOS desktop environments.
- Prometheus (optional, for metric monitoring).

## Technologies and Design

- **Language:** Java
- **GUI Toolkit:** Swing
- **Visualisation Library:** JFreeChart
- **Monitoring:** Prometheus Java Client
- **Design Patterns:**
  - *Builder Pattern* – for consistent and maintainable UI component creation.
  - *Composite Pattern* – used in the binary search tree structure for job searching.
  - *MVC Architecture* – the program separates concerns logically into Model (JobAdvert, JobTableModel, Search), View (GUI), and Controller (event listeners within the GUI).

## Architecture and Structure

The architecture follows an MVC-inspired separation:

- **Model:** `JobAdvert`, `JobTableModel`, and `Search` manage the data and business logic.
- **View:** The `GUI` class, implemented with Swing, is responsible for rendering and updating the interface.
- **Controller:** Action listeners and event handling logic are implemented within the GUI's `initListeners()` method.

## Example Usage

1. Launch the application via the `Main` class.
2. Use the control panel to load jobs from a CSV file or add them manually.
3. Enter home latitude and longitude to enable distance-based features.
4. Search or sort the job listings using the interface tools.
5. Visualise job distances on a scatter plot.
6. Save current job listings to a CSV file.

## CSV Format Example

```
Job ID,Title,Company,Skills,Latitude,Longitude,Salary,Deadline
1,Software Engineer,Google,Java;Python,37.4219983,-122.084,150000,11/02/2025
```

## Testing

The software includes a method `testJobTableModelWithFakeData(count)` for performance and usability testing. Prometheus metrics are available via `http://localhost:8000/metrics`.

## Contribution and Licensing

This project is open-source and licensed under the MIT License. Contributions are encouraged and welcome. Interested contributors may fork the repository, implement features or improvements, and submit a pull request.

## Contact

Author: Liliia Rudenko
Institution: University of the West of Scotland  
Email: b01147264@studentmail.uws.ac.uk
