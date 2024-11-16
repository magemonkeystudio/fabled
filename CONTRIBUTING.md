# Contributing to Fabled  

Welcome, and thank you for considering contributing to Fabled! Your involvement helps make this project better. This guide will walk you through setting up the project locally, contributing changes, and submitting your work.

---

## Getting Started  

Before you begin, ensure you have the following installed:  

- **Java Development Kit (JDK)** (version 8 or higher)  
- **Maven** (for building the project)  
- A **Spigot** or **Paper Minecraft server** for testing (optional).  

If you’re unfamiliar with setting up a Minecraft server, refer to the [Spigot Documentation](https://www.spigotmc.org/wiki/spigot-installation/) for detailed guidance.  

---

## Local Setup  

Follow these steps to set up the project on your local machine:  

### 1. Clone the Repository  
First, fork the repository and then clone it to your local system:  

```bash
git clone https://github.com/magemonkeystudio/fabled.git
cd fabled
```

### 2. Build the Project
Use Maven to clean and build the project:

```bash
mvn clean install
```
This will generate a JAR file in the target/ directory.

### 3. Test the Plugin (Optional)
To test your build:

- Copy the generated JAR file from the target/ directory to the plugins/ folder of your Minecraft server.
- Start the server.
- Check the server logs to confirm that the plugin has loaded successfully. Look for messages related to Fabled in the console.

---

## Contributing Code

1. **Fork and Branch**
- Fork this repository.
-Create a new branch for your contribution:
```bash
  git checkout -b feature/your-feature-name
```
2. **Make Changes**
- Write clean, well-documented code.
- Ensure your changes align with existing conventions.

3. **Commit Your Changes**
Write clear and concise commit messages:

```bash
git add .
git commit -m "Add a brief description of the change"
git push origin feature/your-feature-name
```

4. **Submit a Pull Request**

Open a pull request (PR) on the main repository. Ensure your PR:

- References any related issues.
- Includes a description of what you’ve changed and why.

  ---

 ## Coding Standards
Follow standard Java conventions.
Use meaningful variable names and comments where necessary.
Ensure all code is tested and free of errors before submission.


## Need Help?
If you encounter any issues or need clarification, feel free to open an issue or reach out to the maintainers.

Thank you for contributing to Fabled! Together, we can build something amazing.
