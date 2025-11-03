🔐 Secure Password Generator (Java CLI)

🛡️ Project Overview

In an era defined by digital credentials, security is paramount. The Secure Password Generator is a robust, command-line utility built in Java designed to create highly random, cryptographically secure passwords that meet modern complexity standards.

Unlike simple randomizers, this tool leverages Java's best-in-class security features to ensure the entropy and unpredictability of every generated string. It's perfect for system administrators, developers, or anyone who needs to quickly generate a strong, custom password directly from their terminal or within a script.

✨ Key Features

Cryptographically Secure Randomness: Utilizes java.security.SecureRandom instead of the less secure standard Random class, ensuring true high-entropy generation.

Guaranteed Inclusion: The core algorithm ensures that at least one character from every selected category (lowercase, uppercase, digits, symbols) is included, guaranteeing compliance with strict password policies.

Dual Operation Modes: Supports both an intuitive Interactive Mode via console prompts and a Command-Line Argument Mode for fast, scriptable generation.

Robust Input Validation: Prevents common errors, such as requesting a password length that is too short to satisfy the required complexity categories.

Customizable Complexity: Users can precisely control the character sets used in the generation process (e.g., exclude symbols or digits).

🛠️ How It Works (The Security Edge)

The generator's strength comes from two key architectural decisions:

High-Entropy Source: The static instance of SecureRandom is initialized once and used throughout. This class blocks until sufficient seed material is gathered from the operating system's entropy pool, making the resulting random numbers unpredictable and resistant to attacks.

The Fill-and-Shuffle Approach:

It first identifies all required character categories (e.g., LOWER, DIGITS, SYMBOLS).

It then places one random character from each required category into a random, non-overlapping position in the password array.

Finally, it fills the remaining slots using the complete pool of allowed characters. This guarantees both complexity and randomness in character placement.

🚀 Getting Started

Prerequisites

You need to have a Java Development Kit (JDK) version 8 or higher installed on your system to compile and run the application.

Compilation

Save the provided code into a file named PasswordGenerator.java.

Compile the source file using the Java compiler:

javac PasswordGenerator.java


Usage: Interactive Mode (Recommended for quick use)

Run the compiled class without any arguments to enter the guided, interactive mode:

java PasswordGenerator


The application will prompt you for the password length and ask simple Y/n questions for each character category:

=== Password Generator ===
Password length (default 16): 20
Include lowercase letters? (Y/n, default Y): y
Include uppercase letters? (Y/n, default Y): y
Include digits? (Y/n, default Y): n
Include symbols? (Y/n, default Y): y

Generated password:
8y$pP@zE(O_mJgW*qFhL!


Usage: Command-Line Mode (Recommended for scripting)

You can pass all required parameters as arguments for non-interactive execution.

Syntax:

java PasswordGenerator <length> <lower> <upper> <digits> <symbols>


The boolean arguments can be any of t/f, true/false, y/n, yes/no, or 1/0.

Examples:

Command

Description

Example Output

java PasswordGenerator 12 t t t t

12 characters, all categories included.

G5b@fQ2j9m(I

java PasswordGenerator 8 t f t f

8 characters, only lower and digits.

8m4h7k0j

java PasswordGenerator 24 n y t y

24 characters, no lowercase letters.

!T^Y&5S9@K+P=M*V7E2L6G(H

java PasswordGenerator 3 t f f f

Error: Length too short for selected categories (requires minimum 4).

Error: Length too short to include...

🏗️ Project Architecture

The project is contained within a single class, PasswordGenerator.java, for simplicity and portability.

Component

Responsibility

secureRandom

Provides the non-deterministic, secure source of randomness.

LOWER, UPPER, DIGITS, SYMBOLS

Static constants defining the character pools.

generatePassword (Method)

The core generation algorithm: input validation, character pool construction, guaranteed inclusion, and slot filling.

main (Method)

Handles command-line arguments, manages the interactive session loop, and displays final output or errors.

🤝 Contributing

We welcome contributions to make this generator even more robust and user-friendly!

Fork this repository.

Create your feature branch (git checkout -b feature/AmazingFeature).

Commit your changes (git commit -m 'Add some AmazingFeature').

Push to the branch (git push origin feature/AmazingFeature).

Open a Pull Request.

Areas for Future Work:

Implement a passphrase mode (using dictionary words and random separators).

Add a GUI layer (e.g., using Swing or JavaFX) for non-terminal use.

Allow customization of the symbol pool via a configuration file.

📄 License

Distributed under the MIT License. See LICENSE (not included in this file, but assumed) for more information.