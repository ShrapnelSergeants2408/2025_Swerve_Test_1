// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import java.util.Map;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveShuffleboardManager {
    private final SwerveSubsystem swerve;
    private final PowerDistribution pdh;
    private final CommandXboxController driverController;
    
    // Shuffleboard tabs
    private final ShuffleboardTab driveTab;
    private final ShuffleboardTab powerTab;
    private final ShuffleboardTab moduleTab;
    
    // Field widget
    private final Field2d field;
    
    // Module state widgets
    private final ModuleStateWidget[] moduleWidgets;
    
    // Cached entries for frequent updates
    private final GenericEntry[] moduleSpeedEntries;
    private final GenericEntry[] moduleAngleEntries;
    private final GenericEntry batteryVoltage;
    private final GenericEntry totalCurrent;
    
    public SwerveShuffleboardManager(SwerveSubsystem swerve, CommandXboxController controller) {
        this.swerve = swerve;
        this.driverController = controller;
        this.pdh = new PowerDistribution();
        
        // Create tabs
        driveTab = Shuffleboard.getTab("Drive");
        powerTab = Shuffleboard.getTab("Power");
        moduleTab = Shuffleboard.getTab("Modules");
        
        // Initialize field widget
        field = new Field2d();
        driveTab.add("Field", field)
                .withWidget(BuiltInWidgets.kField)
                .withSize(6, 4)
                .withPosition(0, 0);
        
        // Initialize module widgets
        moduleWidgets = new ModuleStateWidget[4];
        moduleSpeedEntries = new GenericEntry[4];
        moduleAngleEntries = new GenericEntry[4];
        
        setupModuleWidgets();
        setupPowerTab();
        setupDriveTab();
    }
    
    private void setupModuleWidgets() {
        for (int i = 0; i < 4; i++) {
            ShuffleboardLayout moduleLayout = moduleTab
                .getLayout("Module " + i, BuiltInLayouts.kGrid)
                .withSize(2, 4)
                .withPosition(i * 2, 0);
                
            moduleSpeedEntries[i] = moduleLayout
                .add("Speed", 0.0)
                .withWidget(BuiltInWidgets.kNumberBar)
                .withProperties(Map.of("min", -5, "max", 5))
                .getEntry();
                
            moduleAngleEntries[i] = moduleLayout
                .add("Angle", 0.0)
                .withWidget(BuiltInWidgets.kDial)
                .withProperties(Map.of("min", -180, "max", 180))
                .getEntry();
                
            // Add current readings
            moduleLayout.addNumber("Drive Current", () -> pdh.getCurrent(i * 2))
                      .withWidget(BuiltInWidgets.kNumberBar)
                      .withProperties(Map.of("min", 0, "max", 40));
                      
            moduleLayout.addNumber("Turn Current", () -> pdh.getCurrent(i * 2 + 1))
                      .withWidget(BuiltInWidgets.kNumberBar)
                      .withProperties(Map.of("min", 0, "max", 20));
        }
    }
    
    private void setupPowerTab() {
        batteryVoltage = powerTab.add("Battery Voltage", 0.0)
            .withWidget(BuiltInWidgets.kVoltageView)
            .withProperties(Map.of("min", 0, "max", 13))
            .getEntry();
            
        totalCurrent = powerTab.add("Total Current", 0.0)
            .withWidget(BuiltInWidgets.kNumberBar)
            .withProperties(Map.of("min", 0, "max", 120))
            .getEntry();
            
        // Add power graph
        powerTab.add("Power Usage", new PowerGraph())
                .withWidget(BuiltInWidgets.kGraph)
                .withSize(3, 3)
                .withPosition(0, 3);
    }
    
    private void setupDriveTab() {
        // Add gyro widget
        driveTab.add("Gyro", new GyroWidget())
                .withWidget(BuiltInWidgets.kGyro)
                .withSize(2, 2)
                .withPosition(6, 0);
                
        // Add velocity vectors
        driveTab.add("Robot Velocity", new VelocityWidget())
                .withWidget(BuiltInWidgets.kGraph)
                .withSize(3, 3)
                .withPosition(6, 2);
                
        // Add controller inputs
        ShuffleboardLayout inputs = driveTab
            .getLayout("Controller", BuiltInLayouts.kGrid)
            .withSize(2, 4)
            .withPosition(9, 0);
            
        inputs.addNumber("Left X", () -> driverController.getLeftX());
        inputs.addNumber("Left Y", () -> driverController.getLeftY());
        inputs.addNumber("Right X", () -> driverController.getRightX());
        inputs.addNumber("Right Y", () -> driverController.getRightY());
    }
    
    public void updateTelemetry() {
        updateField();
        updateModules();
        updatePower();
    }
    
    private void updateField() {
        field.setRobotPose(swerve.getPose());
        
        // Update module positions on field
        Pose2d[] modulePoses = new Pose2d[4];
        for (int i = 0; i < 4; i++) {
            Translation2d modulePosition = swerve.getModulePositions()[i];
            Rotation2d moduleRotation = swerve.getModuleStates()[i].angle;
            modulePoses[i] = new Pose2d(modulePosition, moduleRotation);
        }
        field.getObject("modules").setPoses(modulePoses);
    }
    
    private void updateModules() {
        SwerveModuleState[] states = swerve.getModuleStates();
        for (int i = 0; i < states.length; i++) {
            moduleSpeedEntries[i].setDouble(states[i].speedMetersPerSecond);
            moduleAngleEntries[i].setDouble(states[i].angle.getDegrees());
        }
    }
    
    private void updatePower() {
        batteryVoltage.setDouble(pdh.getVoltage());
        totalCurrent.setDouble(pdh.getTotalCurrent());
    }
}