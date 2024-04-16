// RobotBuilder Version: 6.1
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: Robot.

package frc.robot;

import static frc.robot.Constants.CLOSE_ARM_SPEED;
import static frc.robot.Constants.FOLD;
import static frc.robot.Constants.FOLD_OF_SET;
import static frc.robot.Constants.GROUND;
import static frc.robot.Constants.ID_INTAKE;
import static frc.robot.Constants.INTAKE_POWER;
import static frc.robot.Constants.JOYSTICK_PORT;
import static frc.robot.Constants.KG;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.A;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.B;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.LEFT_STICK_X;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.LEFT_STICK_Y;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.RIGHT_STICK_X;
import static frc.robot.POM_lib.Joysticks.JoystickConstants.Y;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in 
 * the project.
 */
public class Robot extends TimedRobot {

    private Command m_autonomousCommand;

    private RobotContainer m_robotContainer;

    private CANSparkMax intakeMotor = new CANSparkMax(ID_INTAKE, MotorType.kBrushless);
    Joystick joystick = new Joystick(JOYSTICK_PORT); 

    private final CANSparkMax liftMotor = new CANSparkMax(5, com.revrobotics.CANSparkLowLevel.MotorType.kBrushless);
    private RelativeEncoder encoder = liftMotor.getEncoder();
    private ArmFeedforward ff = new ArmFeedforward(0, KG, 0);

    DigitalInput foldSwitch = new DigitalInput(FOLD);
    DigitalInput groundSwitch = new DigitalInput(GROUND);

    private boolean toClose = false;

    WPI_TalonSRX leftTalonSPX = new WPI_TalonSRX(2);
    WPI_VictorSPX leftVictorSPX = new WPI_VictorSPX(1);
    WPI_TalonSRX rightTalonSPX = new WPI_TalonSRX(4);
    WPI_VictorSPX rightVictorSPX = new WPI_VictorSPX(3);

    private final DifferentialDrive m_drive =
      new DifferentialDrive(leftTalonSPX::set, rightTalonSPX::set);


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */

    public double resistGravity(){
        return ff.calculate(encoder.getPosition(), 0);
    }
    
    @Override
    public void robotInit() {
        // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        m_robotContainer = RobotContainer.getInstance();
        HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_RobotBuilder);
        enableLiveWindowInTest(true);
        encoder.setPositionConversionFactor((1.0 / 50) * (16.0 / 42) * 2 * Math.PI);

        leftVictorSPX.follow(leftTalonSPX);
        rightVictorSPX.follow(rightTalonSPX);
    }

    /**
    * This function is called every robot packet, no matter the mode. Use this for items like
    * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
    *
    * <p>This runs after the mode specific periodic functions, but before
    * LiveWindow and SmartDashboard integrated updating.
    */
    @Override
    public void robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run();
        SmartDashboard.putNumber("arm encoder", encoder.getPosition());
        SmartDashboard.putNumber("gravity resist", resistGravity());

        SmartDashboard.putBoolean("fold Switch", !foldSwitch.get());
        SmartDashboard.putBoolean("ground Switch", !groundSwitch.get());

        if(!foldSwitch.get()){
            encoder.setPosition(FOLD_OF_SET);
        }
    }


    /**
    * This function is called once each time the robot enters Disabled mode.
    */
    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    /**
    * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
    */
    @Override
    public void autonomousInit() {
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    /**
    * This function is called periodically during autonomous.
    */
    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
    }

    /**
     * This function is called periodically during operator control.
     */

    @Override
    public void teleopPeriodic() {
        if(joystick.getRawButton(B)){
            intakeMotor.set(INTAKE_POWER);
        }
        else if(joystick.getRawButton(A)){
            intakeMotor.set(-INTAKE_POWER);
        }
        else{
            intakeMotor.set(0);
        }


        if(joystick.getRawButton(Y)){
            toClose = true;
        }
        if(!foldSwitch.get()){
            toClose = false;
        }

        if(toClose){
            liftMotor.set(resistGravity() - CLOSE_ARM_SPEED);
        }
        else{
            liftMotor.set(resistGravity());
        }

        m_drive.arcadeDrive(joystick.getRawAxis(LEFT_STICK_Y), joystick.getRawAxis(RIGHT_STICK_X));


    }

    @Override
    public void testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll();
    }

    /**
    * This function is called periodically during test mode.
    */
    @Override
    public void testPeriodic() {
    }

}
