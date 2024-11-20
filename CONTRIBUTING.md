# Contributing to Fabled  

Welcome, and thank you for considering contributing to Fabled! Your involvement helps make this project better. This guide will walk you through setting up the project locally, contributing changes, and submitting your work.

---

## Getting Started  

Before you begin, ensure you have the following installed:  

- **Java Development Kit (JDK)** (version 14 or higher)  
- **Maven** (for building the project)  
- A **Spigot** or **Paper Minecraft server** for testing (optional).  

If you’re unfamiliar with setting up a Minecraft server, refer to the [Spigot Documentation](https://www.spigotmc.org/wiki/spigot-installation/) for detailed guidance.  

---

## Local Setup  

Follow these steps to set up the project on your local machine:  

### 1. Clone the Repository  

First, fork the repository and then clone it to your local system:  

```bash
git clone https://github.com/YOUR_USERNAME/fabled.git
cd fabled
```

### 2. Build the Project

Use Maven to clean and build the project:

```bash
mvn clean install
```
Fabled is designed to build as part of a submodule to `magemonkey-parent`, so by default, the output directory is `../build`.

### 3. Test the Plugin (Optional)

To test your build:
- Copy the generated JAR file from the `../build` directory to the `plugins` folder of your Minecraft server.
  - Note that Codex is required to run Fabled.
- Start the server.
- Check the server logs to confirm that the plugin has loaded successfully. Look for messages related to Fabled in the console.

---

## Contributing Code

### Fork and Branch

- Fork this repository.
- Create a new branch for your contribution:
```bash
git checkout -b feature/your-feature-name
```

### Make Changes
- Write clean, well-documented code.
- Ensure your changes align with existing conventions.

### Commit Your Changes
Write clear and concise commit messages:

```bash
git add .
git commit -m "Add a brief description of the change"
git push origin feature/your-feature-name
```

### Submit a Pull Request

Open a pull request (PR) on the main repository against the `dev` branch. Ensure your PR:

- References any related issues.
- Includes a description of what you’ve changed and why.
- If you're adding a new component (trigger, condition, mechanic, etc.), create an additional PR to the `editor` branch 
to add the component to the web editor. [Fabled Editor](https://github.com/magemonkeystudio/fabled/tree/editor)

---

## Coding Standards
Follow standard Java conventions.

Please follow the existing code format. Codestyle files are provided for
[IntelliJ IDEA](https://github.com/magemonkeystudio/fabled/blob/dev/IntelliJ_codestyle.xml) and
[Eclipse](https://github.com/magemonkeystudio/fabled/blob/dev/Eclipse_codestyle.xml) in the root directory
for you to import into your IDE.

Use meaningful variable names and comments where necessary.

Ensure all code is tested and free of errors before submission.


## Need Help?
If you encounter any issues or need clarification, feel free to open an issue or reach out on Discord.

Thank you for contributing to Fabled! Together, we can build something amazing.

[![Discord](https://dcbadge.vercel.app/api/server/6UzkTe6RvW?style=flat)](https://discord.gg/6UzkTe6RvW)
