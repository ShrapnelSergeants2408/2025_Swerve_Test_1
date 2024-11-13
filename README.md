# FRC Team 2408 - 2025 Swerve Drive Development

## Overview
This repository contains development and testing code for Team 2408's swerve drive implementation for the 2025 season. This is an experimental/practice codebase focused on prototyping and testing swerve drive configurations, and serves as a development ground for our competition robot's drive system.

## Purpose
- Develop and test swerve drive implementations
- Experiment with different control algorithms
- Test hardware configurations
- Document findings for competition implementation
- Train team members on swerve drive concepts

## Getting Started

### Required Software
- WPILib 2024.1.1 or newer
- Visual Studio Code
- Git
- WPILib VS Code extension
- REVLib vendor library
- Phoenix vendor library

### Development Environment Setup
1. Install required software:
   ```bash
   # Clone repository
   git clone https://github.com/FRCTeam2408/2025_Swerve_Test_1.git
   cd 2025_Swerve_Test_1
   
   # Build project
   ./gradlew build
   ```

2. Configure VSCode:
   - Open project in VSCode
   - Install recommended extensions
   - Set Java path to WPILib JDK
   - Enable format on save

### Hardware Test Configuration
```
Swerve Module Layout:
Front Left (ID: 1)    Front Right (ID: 2)
Back Left  (ID: 3)    Back Right  (ID: 4)

Components per module:
- Drive Motor: [Type TBD]
- Turn Motor: [Type TBD]
- Absolute Encoder: [Type TBD]
- Relative Encoder: [Type TBD]

Additional Sensors:
- Gyro: [Type TBD]
```

## Project Structure
```
src/main/java/frc/robot/
├── subsystems/
│   └── swerve/          # Swerve drive implementation
│       ├── module/      # Individual module control
│       └── states/      # Module states and kinematics
├── commands/
│   └── swerve/          # Swerve drive commands
├── constants/           # Configuration constants
└── utils/              # Utility classes and helper functions
```

## Development Process

### Git Workflow
1. Create a branch for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make changes and commit:
   ```bash
   git add .
   git commit -m "type(scope): description
   
   - Detail 1
   - Detail 2"
   ```

3. Push changes:
   ```bash
   git push origin feature/your-feature-name
   ```

4. Create pull request through GitHub

### Testing Requirements
- All code must include unit tests
- Test in simulation before deploying
- Document all test results
- Include comment-based documentation

## Safety Protocols

### Code Safety
- Motor limits must be implemented
- Soft limits must be configured
- Emergency stop must be tested
- Watchdog must be enabled
- All autonomous functions must include safety bounds

### Testing Safety
1. Always test in simulation first
2. When testing on robot:
   - Clear testing area
   - Enable at low speeds first
   - Have e-stop readily available
   - Document all unexpected behavior

## Building and Testing

### Build Commands
```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Run simulation
./gradlew simulateJava

# Deploy to robot
./gradlew deploy
```

### Testing Process
1. Run unit tests locally
2. Test in simulation
3. Test on practice bot
4. Document results
5. Update relevant documentation

## Contributing

### Pull Request Process
1. Create feature branch
2. Write code and tests
3. Update documentation
4. Create pull request
5. Address review feedback
6. Merge when approved

### Code Standards
- Follow WPILib style guide
- Use meaningful variable names
- Comment all complex logic
- Include Javadoc for public methods
- Keep methods focused and small

## Documentation

### Required Documentation
- Method documentation (Javadoc)
- Complex logic explanation
- Test coverage
- Usage examples
- Safety considerations

### Performance Documentation
For each test run:
- Date and code version
- Test conditions
- Performance metrics
- Issues encountered
- Solutions implemented

## Contact and Support

### Team Contacts
- Team Email: [team email]
- Mentors:
  - Dan Hiebert
  - Josh Kupka
  - Stephanie [Last Name]
- Student Lead: Taaliyah

### Additional Resources
- [Team 2408 Wiki](wiki_link)
- [WPILib Swerve Documentation](link)
- [FIRST Robotics Competition Resources](link)
- [Team 2408 Website](team_website)

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
