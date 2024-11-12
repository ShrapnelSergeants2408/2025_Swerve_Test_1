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
import edu.wpi.first.wpilibj.PowerDistribution;
import frc.robot.subsystems.SwerveSubsystem;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.math.geometry.Rotation2d;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import java.util.LinkedList;



public class SwerveShuffleboardManager {
    private final SwerveSubsystem swerve;
    private final PowerDistribution pdh;
    private final CommandXboxController driverController;
    
    // Shuffleboard tabs
    private ShuffleboardTab driveTab; 
    private ShuffleboardTab powerTab;
    private ShuffleboardTab moduleTab;
    
    // Field widget
    private Field2d field;
    
    // Module state widgets
    private ModuleStateWidget[] moduleWidgets;
    
    // Cached entries for frequent updates
    private GenericEntry[] moduleSpeedEntries;
    private GenericEntry[] moduleAngleEntries;
    private GenericEntry batteryVoltage;
    private GenericEntry totalCurrent;
    
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
        
        initializeField();
        initializeModuleArrays();
        setupModuleWidgets();
        setupPowerTab();
        setupDriveTab();
    }
    
    private void setupModuleWidgets() {
        for (int i = 0; i < 4; i++) {
            // Create a final copy of the index for use in lambdas
            final int moduleIndex = i;  // This is effectively final

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
            moduleLayout.addNumber("Drive Current", () -> pdh.getCurrent(moduleIndex * 2))
                      .withWidget(BuiltInWidgets.kNumberBar)
                      .withProperties(Map.of("min", 0, "max", 40));
                      
            moduleLayout.addNumber("Turn Current", () -> pdh.getCurrent(moduleIndex * 2 + 1))
                      .withWidget(BuiltInWidgets.kNumberBar)
                      .withProperties(Map.of("min", 0, "max", 20));
        }
    }
    
    private void setupPowerTab() {

        // Pass pdh to PowerGraph constructor
        PowerGraph powerGraph = new PowerGraph(pdh);

        batteryVoltage = powerTab.add("Battery Voltage", 0.0)
            .withWidget(BuiltInWidgets.kVoltageView)
            .withProperties(Map.of("min", 0, "max", 13))
            .getEntry();
            
        totalCurrent = powerTab.add("Total Current", 0.0)
            .withWidget(BuiltInWidgets.kNumberBar)
            .withProperties(Map.of("min", 0, "max", 120))
            .getEntry();
            
        // Add power graph
        powerTab.add("Power Usage", powerGraph)
                .withWidget(BuiltInWidgets.kGraph)
                .withSize(3, 3)
                .withPosition(0, 3);
    }
    
    private void setupDriveTab() {

        // Add gyro widget
        driveTab.add("Gyro", new GyroWidget(swerve))
                .withWidget(BuiltInWidgets.kGyro)
                .withSize(2, 2)
                .withPosition(6, 0);
                
        // Add velocity vectors
        driveTab.add("Robot Velocity", new VelocityWidget(swerve))
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

    // Now let's implement each custom widget:

    /**
     * Custom Gyro visualization widget
     */
    public class GyroWidget implements Sendable {
        private double angle = 0;
        private final SwerveSubsystem swerve;

       public GyroWidget(SwerveSubsystem swerve) {
            this.swerve = swerve;
       }

       @Override
        public void initSendable(SendableBuilder builder) {
            builder.setSmartDashboardType("Gyro");
            builder.addDoubleProperty("Value", () -> swerve.getHeading().getDegrees(), null);
        }
    }

    /**
     * Custom velocity visualization widget
     */
    public class VelocityWidget implements Sendable {
        private final SwerveSubsystem swerve;
        private final LinkedList<Double> velocityHistory = new LinkedList<>();
        private static final int HISTORY_SIZE = 50;

       public VelocityWidget(SwerveSubsystem swerve) {
           this.swerve = swerve;
       }

        @Override
        public void initSendable(SendableBuilder builder) {
            builder.setSmartDashboardType("Graph");
            builder.addDoubleProperty("Velocity X", 
                () -> swerve.getFieldVelocity().vxMetersPerSecond, null);
            builder.addDoubleProperty("Velocity Y", 
                () -> swerve.getFieldVelocity().vyMetersPerSecond, null);
            builder.addDoubleProperty("Angular Velocity", 
                () -> swerve.getFieldVelocity().omegaRadiansPerSecond, null);
        }
    }

    /**
     * Custom power monitoring widget
     */
    public class PowerGraph implements Sendable {
     private final PowerDistribution pdh;
        private final LinkedList<Double> powerHistory = new LinkedList<>();
        private static final int HISTORY_SIZE = 50;

     public PowerGraph(PowerDistribution pdh) {
          this.pdh = pdh;
      }

      @Override
     public void initSendable(SendableBuilder builder) {
         builder.setSmartDashboardType("Graph");
         builder.addDoubleProperty("Total Power", () -> pdh.getTotalPower(), null);
         builder.addDoubleProperty("Voltage", () -> pdh.getVoltage(), null);
         builder.addDoubleProperty("Current", () -> pdh.getTotalCurrent(), null);
      }
    }

    /**
     * Custom module state visualization widget
     */
    public class ModuleStateWidget implements Sendable {
       private final int moduleIndex;
       private final SwerveSubsystem swerve;

       public ModuleStateWidget(int moduleIndex, SwerveSubsystem swerve) {
           this.moduleIndex = moduleIndex;
           this.swerve = swerve;
       }

       @Override
       public void initSendable(SendableBuilder builder) {
           builder.setSmartDashboardType("Swerve Module");
           builder.addDoubleProperty("Speed", 
               () -> swerve.getModuleStates()[moduleIndex].speedMetersPerSecond, null);
          builder.addDoubleProperty("Angle", 
              () -> swerve.getModuleStates()[moduleIndex].angle.getDegrees(), null);
      }
    }

    // Private initialization methods
    private void initializeField() {
        field = new Field2d();
        driveTab.add("Field", field)
                .withWidget(BuiltInWidgets.kField)
                .withSize(6, 4)
                .withPosition(0, 0);
    }

    private void initializeModuleArrays() {
        moduleWidgets = new ModuleStateWidget[4];
        moduleSpeedEntries = new GenericEntry[4];
        moduleAngleEntries = new GenericEntry[4];
        for (int i = 0; i < 4; i++) {
            moduleWidgets[i] = new ModuleStateWidget(i, swerve);
        }
    }

}