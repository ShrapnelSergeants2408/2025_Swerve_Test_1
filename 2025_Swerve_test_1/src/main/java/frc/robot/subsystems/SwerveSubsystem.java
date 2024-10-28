 // Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

//imports for swerve drive setup from json
import java.io.File;
import edu.wpi.first.wpilibj.Filesystem;
import swervelib.parser.SwerveParser;
import swervelib.SwerveDrive;
import edu.wpi.first.math.util.Units;

public class SwerveSubsystem extends SubsystemBase {
  /** Creates a new SwerveSubsystem. */
  public SwerveSubsystem() {
    //Photonvision class for vision processing/odometry
    private Vision vision;

    //Swerve drive object
    private final SwerveDrive swerveDrive;

    //AprilTag field layout
    //TODO: update this once new field layout is released
    private final AprilTagFieldLayout aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();

    //Enable vision odometry updates while driving
    private final boolean visionDriveTest = false;

    // use json files for swerve module setup
    public SwerveSubsystem(File directory)
    {
      //  Angle conversion factor is 360 / (GEAR RATIO * ENCODER RESOLUTION)
      //  Thriftybot gear ratio is 25:1.  
      //  The encoder resolution per motor revolution is 1 per motor revolution.
      //TODO: double check these values
      double angleConversionFactor = SwerveMath.calculateDegreesPerSteeringRotation(25);

      // Motor conversion factor is (PI * WHEEL DIAMETER IN METERS) / (GEAR RATIO * ENCODER RESOLUTION).
      // Use the table from YAGSL thriftybot NEO to determine value
      //TODO: double check these values
      double driveConversionFactor = SwerveMath.calculateMetersPerRotation(Units.inchesToMeters(3), 15);

      System.out.println("\"conversionFactors\": {");
      System.out.println("\t\"angle\": {\"factor\": " + angleConversionFactor + " },");
      System.out.println("\t\"drive\": {\"factor\": " + driveConversionFactor + " }");
      System.out.println("}");

      // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
      SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
      try
      {
        //swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.MAX_SPEED); //TODO: use calculated conversion factors (below)
        // Alternative method if you don't want to supply the conversion factor via JSON files.
        swerveDrive = new SwerveParser(directory).createSwerveDrive(maximumSpeed, angleConversionFactor, driveConversionFactor);
      } 
        catch (Exception e)
      {
        throw new RuntimeException(e);
      }
      swerveDrive.setHeadingCorrection(false); // Heading correction should only be used while controlling the robot via angle.
      swerveDrive.setCosineCompensator(false); //!SwerveDriveTelemetry.isSimulation); // Disables cosine compensation for simulations since it causes discrepancies not seen in real life.
      swerveDrive.setAngularVelocityCompensation(true,
                                                 true,
                                                 0.1); //Correct for skew that gets worse as angular velocity increases. Start with a coefficient of 0.1.
      swerveDrive.setModuleEncoderAutoSynchronize(false,
                                                  1); // Enable if you want to resynchronize your absolute encoders and motor encoders periodically when they are not moving.
      swerveDrive.pushOffsetsToEncoders(); // Set the absolute encoder to be used over the internal encoder and push the offsets onto it. Throws warning if not possible
      
      if (visionDriveTest)
      {
        setupPhotonVision();
        // Stop the odometry thread if we are using vision that way we can synchronize updates better.
        swerveDrive.stopOdometryThread();
      }
      setupPathPlanner();
    }

    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
