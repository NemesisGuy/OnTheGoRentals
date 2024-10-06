# 📊 CodeCritical

### 🚀 A Comprehensive Java Code Analysis Tool

Welcome to **CodeCritical**, a powerful and flexible tool designed to give you in-depth insights into your Java codebase. Track important metrics like lines of code, functions, classes, maintainability index, and much more. This tool is designed for developers and teams aiming to measure and improve the quality, maintainability, and readability of their code.

## 🔥 Key Features of CodeCritical

### 1. **File Line Counting**
Analyze each `.java` file in your project to gather information such as:
- **Total Lines**: Number of total lines in the file.
- **Code Lines**: Count of lines that represent actual code (excluding comments and empty lines).
- **Comment Lines**: Count of comment lines, including single-line (`//`) and multi-line comments (`/* */`).
- **Blank Lines**: Automatically skips over blank lines, focusing on the meaningful parts of your code.

> 📊 **Benefit**: Get a clear picture of the structure and scale of your codebase.

### 2. **Function Counting**
Detect and count the number of function or method signatures in each file.
- Identifies all functions by scanning for patterns like `public`, `private`, `protected`, `static`, etc.
- Useful for determining code complexity and coverage.

> 🔧 **Benefit**: Easily track function count to evaluate complexity or assist with unit testing coverage.

### 3. **Class and Interface Detection**
Automatically detects and counts the number of `classes` and `interfaces` declared in each file.
- Keeps track of object-oriented components, allowing you to assess the balance of interfaces vs. classes.

> 🏗️ **Benefit**: Quickly understand the architecture of your project, identifying class-heavy or interface-heavy areas.

### 4. **Code Duplication Detection**
Leveraging a **Code Duplication Detector**, this feature searches for identical blocks of code within your files to help you spot potential areas of code repetition and redundancy.
- Detects duplicate code blocks within individual files.
- Helps enforce DRY (Don't Repeat Yourself) principles, making the codebase easier to maintain.

> ✂️ **Benefit**: Improve code maintainability by eliminating redundant code and reducing future technical debt.

### 5. **Maintainability Index Calculation**
Calculate the **Maintainability Index** (MI) of your code. This score provides a quantitative measure of how maintainable your code is based on:
- **Lines of Code** (LOC)
- **Cyclomatic Complexity** (how complex the control flow is)
- **Comment Density** (proportion of comments to code)

The result is an easy-to-understand score:
- **High** (>80): Very maintainable.
- **Moderate** (50-80): Could use some improvements.
- **Low** (<50): Refactoring recommended!

> 📈 **Benefit**: Get a clear, easy-to-read score that helps prioritize refactoring and improvements to maintainability.

### 6. **Markdown Report Generation**
All results are output in a **Markdown-formatted table**, providing an easy-to-read, professional report of the analyzed files. This includes:
- File name
- Total lines of code
- Code, comment, and function count
- Duplicates found
- Maintainability Index

> 📑 **Benefit**: Perfect for GitHub repositories! Easily track code metrics over time by committing the generated Markdown reports for continuous improvement.

### 7. **Grand Totals**
At the end of the analysis, the tool prints out **Grand Totals** of all the key metrics for the entire codebase:
- Total number of files analyzed
- Total lines of code, comment lines, and function count
- Maintainability index across the entire project

> 🏅 **Benefit**: Keep track of project-wide trends in your codebase, allowing you to measure progress as you refactor and grow.

---

## 🛠️ Future Features

- **Cyclomatic Complexity Breakdown**: Include a per-function breakdown of cyclomatic complexity.
- **Extended Language Support**: Expand analysis to additional languages like Python, JavaScript, and more.
- **Real-time Web Dashboard**: Visualize your code metrics in real-time through a web-based dashboard.

---

## 👨‍💻 Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request if you'd like to improve CodeCritical or add new features.

## 👥 Authors

- **Peter Buckingham** - [NemesisGuy](https://github.com/NemesisGuy)

---

## 📄 License
This project is licensed under the MIT License – see the [LICENSE](LICENSE.md) file for details.

---

## 🙌 Acknowledgments

This tool is part of a larger suite of software development tools aimed at improving software quality, maintainability, and developer productivity.

---

🎉 **Happy coding, and may your code always be clean, maintainable, and DRY!**
