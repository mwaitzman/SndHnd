package agents;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import waves.GAppxBezWaveform;
import waves.GRoughDraftWaveSwitch;
import waves.GSawtoothWaveform;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicClampedCoefficient;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.ClampedCoefficient;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.InterpolationPoint;
import core.NonClampedCoefficient;
import core.NoteDesc;
import core.NoteInitializer;
import core.NoteTable;
import core.PhaseDistortionPacket;
import core.SongData;
import core.TrackFrame;
import core.VibratoParameters;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ClampedCoefficientRemodulator;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GAnalogPhaseDistortionWaveForm;
import cwaves.Inverter;
import cwaves.SineWaveform;


/**
 * 
 * Agent emulating the playing of string 1 (the low E string) on a Squirer guitar using prettymuch only inverters for the timbre.  This has been largely supplanted by ExprAgentS1.  However it could still be used in cases where a timbre more synthetic sounding than ExprAgentS1 is desired.
 * 
 * Note: the playing of the string always uses the open string timbre as this is considered to be a superior timbre (although actual string playing might not generate this timbre).
 * 
 * It is also true that actual instrument playing doesn't usually produce the default vibrato generated by this agent, but this can be turned off by changing the GUI parameter.
 * 
 * Timbre is intended to emulate the output of the stock guitar amplifier with default knob settings and with the "overdrive" button turned on.  Solutions were intentionally chosen that eliminate the background noise of the original amplifier.
 * 
 * @author tgreen
 *
 */
public class SquirerAgentS1 extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();

	/**
	 * The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	protected double minDecayTimeBeats = 4.0;

	/**
	 * For an instrument amplitude decay that is interrupted before minDecayTimeBeats, the number of waves over which the interruption of the amplitude takes place.
	 */
	protected double cutoffTimeWaves = 2.5;

	/**
	 * The duration of the initial attack of a note in waves.
	 */
	protected double attackTimeWaves = 3.0 /* 5.0 */;
	
	/**
	 * Sets the duration of the initial attack of a note in waves.
	 * @param in The duration of the initial attack of a note in waves.
	 */
	public void setAttackTimeWaves( double in )
	{
		attackTimeWaves = in;
	}
	
	
	@Override public void setInitialNoteEnvelope( NoteDesc nd , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		if( !( nd.isUserDefinedVibrato() ) )
		{
			nd.setVibratoParams( new VibratoParameters( vibratoParams ) );
		}
		if( !( nd.isUserDefinedNoteEnvelope() ) )
		{
			PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
			bezAC.getInterpolationPoints().clear();
			ZSoundAgentS3.createInitialEnvelope( bezAC.getInterpolationPoints() );
			nd.getVibratoParams().buildTremolo( nd , nxt , minDecayTimeBeats , bezAC.getInterpolationPoints() , core );
			bezAC.updateAll();
			BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
			nd.setNoteEnvelope( bezA );
		}
	}

	/**
	 * Produces a representation of how a musician would play an instrument for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote(NoteDesc note1, NoteDesc note2) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, note2.getStartBeatNumber());
		double decayTimeBeatsFinal = note1.getActualEndBeatNumber() - note1.getStartBeatNumber();
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );

		double ratio = decayTimeBeatsFinal / decayTimeBeatsPlay;
		ClampedCoefficient ci = note1.getNoteEnvelope( core );
		if (ratio < 0.9999999999)
		{
			ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			note1.setWaveEnvelope(buildNoteAttack());
			
			/* ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			double endWaveNumber = note1.getEndWaveNumber();
			double startWaveNumber = endWaveNumber - cutoffTimeWaves;
			NonClampedCoefficient noteAttack = buildNoteAttack();
			NonClampedCoefficient noteDecay = buildNoteDecay(note1,
					startWaveNumber, endWaveNumber);
			NonClampedCoefficient finalEnv = new AmplitudeModulationNonClampedCoeff(
					noteAttack, noteDecay);
			note1.setWaveEnvelope(finalEnv); */
		} else {
			note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
			note1.setWaveEnvelope(buildNoteAttack());
		}

		buildNoteInstrument(note1);
		
		if( SongData.ROUGH_DRAFT_MODE )
		{
			SongData.buildBendInterpPoints(note1,note2,10,minDecayTimeBeats,false,core);
		}
		else
		{
			SongData.buildBendInterpPoints(note1,note2,10,minDecayTimeBeats,false,core);
			// ZSoundAgentS3.buildBend(note,minDecayTimeBeats);
		}
		
	}

	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote(NoteDesc note1) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, 1E+40);
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
		note1.setWaveEnvelope(buildNoteAttack());
		buildNoteInstrument(note1);
		
		if( SongData.ROUGH_DRAFT_MODE )
		{
			SongData.buildBendInterpPoints(note1,null,10,minDecayTimeBeats,false,core);
		}
		else
		{
			SongData.buildBendInterpPoints(note1,null,10,minDecayTimeBeats,false,core);
			// ZSoundAgentS3.buildBend(note,minDecayTimeBeats);
		}
		
	}

	/**
	 * Builds the final decay for a note.
	 * @param note The note for which to build the decay.
	 * @param startWaveNumber The starting wave number of the note.
	 * @param endWaveNumber The ending wave number of the note.
	 * @return The decay coefficient.
	 */
	protected NonClampedCoefficient buildNoteDecay(NoteDesc note,
			double startWaveNumber, double endWaveNumber) {
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData
				.buildGradualDecayBezier(20, startWaveNumber, endWaveNumber);
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat(
				bezDrive);
		return (bzC);
	}

	/**
	 * Builds the initial attack for a note.
	 * @return The attack.
	 */
	protected NonClampedCoefficient buildNoteAttack() {
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData
				.buildGradualAttackBezier(20, 0.0, attackTimeWaves);
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat(
				bezDrive);
		return (bzC);
	}
	
	
	/**
	 * Returns an adjustment for primary timbre coefficient values.  This is an estimate based on critical listening.
	 * @param val The original primary timbre coefficient value.
	 * @return The adjusted primary timbre coefficient value.
	 */
	double dvalPrim( double in )
	{
		return( ( in - 0.75 ) / ( 0.84 - 0.75 ) );
	}
	
	
	/**
	 * Returns an adjustment for secondary timbre coefficient values.  This is an estimate based on critical listening.
	 * @param val The original secondary timbre coefficient value.
	 * @return The adjusted secondary timbre coefficient value.
	 */
	double dvalSec( double in )
	{
		return( ( in - 0.09 ) / ( 0.25 - 0.09 ) );
	}

	
	/**
	 * Builds a coefficient for the primary timbre.
	 * @return The coefficient for the primary timbre.
	 */
	protected NonClampedCoefficient buildPrimaryCoeff() {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.75 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.86 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 ,
		 * 0.79 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 13.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 15.0 , 0.75 ) );
		 */

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.75 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.82 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 ,
		 * 0.86 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 21.0 , 0.79 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 25.0 , 0.75 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 ,
		 * 0.75 ) );
		 */

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalPrim(/*0.75*/0.88)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, dvalPrim(0.75)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, dvalPrim(0.75)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(7.5, dvalPrim(0.82)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalPrim(0.86)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalPrim(0.82)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalPrim(0.84)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalPrim(0.84)));

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		 * 
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 ,
		 * 0.0 ) );
		 */

		bezAC.updateAll();

		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat(
				bezAC);

		return (coeff);
	}

	/**
	 * Builds a coefficient for the secondary timbre.
	 * @return The coefficient for the secondary timbre.
	 */
	protected NonClampedCoefficient buildSecondaryCoeff() {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.25 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.09 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 ,
		 * 0.23 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 13.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 15.0 , 0.25 ) );
		 */

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.25 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.15 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 ,
		 * 0.09 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 21.0 , 0.23 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 25.0 , 0.25 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 ,
		 * 0.25 ) );
		 */

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalSec(/*0.25*/0.06)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, dvalSec(0.25)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, dvalSec(0.25)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(7.5, dvalSec(0.15)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalSec(0.09)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalSec(0.15)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalSec(0.13)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalSec(0.13)));

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 1.0 ) );
		 * 
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 ,
		 * 1.0 ) );
		 */

		bezAC.updateAll();

		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat(
				bezAC);

		return (coeff);
	}
	
	
	/**
	 * Gets the initial waveform for the secondary timbre.
	 * @return The waveform for the secondary timbre.
	 */
	protected WaveForm genWaveB( )
	{
		final double maxDivisor = 4.5;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.6641604010025063 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.5338345864661654 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.002506265664160401 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.1 );
		/* PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.17293233082706766 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		/* PhaseDistortionPacket[] pdccxk = { pdcxk };
		PhaseDistortionPacket[] pdccxl = { pdcxl };
		PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new Inverter(pdccxf);
		WaveForm invg = new Inverter(pdccxg);
		WaveForm invh = new Inverter(pdccxh);
		WaveForm invi = new Inverter(pdccxi);
		WaveForm invj = new Inverter(pdccxj);
		/* WaveForm invk = new Inverter(pdccxk);
		WaveForm invl = new Inverter(pdccxl);
		WaveForm invm = new Inverter(pdccxm); */
		NonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( -0.9367088607594937 / maxDivisor );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.17721518987341772 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.08860759493670886 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.759493670886076 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 3.25 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.43037974683544306 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.4936708860759494 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		return( wave3 );
	}
	
	
	/**
	 * Gets the initial waveform for the primary timbre.
	 * @return The waveform for the primary timbre.
	 */
	protected WaveForm genWaveA( )
	{
		final double maxDivisor = 4.0;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.6641604010025063 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.7944862155388471 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.3182957393483709 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.1 );
		/* PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.17293233082706766 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		/* PhaseDistortionPacket[] pdccxk = { pdcxk };
		PhaseDistortionPacket[] pdccxl = { pdcxl };
		PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new Inverter(pdccxf);
		WaveForm invg = new Inverter(pdccxg);
		WaveForm invh = new Inverter(pdccxh);
		WaveForm invi = new Inverter(pdccxi);
		WaveForm invj = new Inverter(pdccxj);
		/* WaveForm invk = new Inverter(pdccxk);
		WaveForm invl = new Inverter(pdccxl);
		WaveForm invm = new Inverter(pdccxm); */
		NonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( -0.9113924050632911 / maxDivisor );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.11392405063291139 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.0759493670886076 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 1.75 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.43037974683544306 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.4936708860759494 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		return( wave3 );
	}
	
	
	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note) throws Throwable
	{
		currentNote = note;
		buildNoteInstrument(note, false);
		if( SongData.roughDraftMode == SongData.ROUGH_DRAFT_MODE_BEZ_APPROX )
		{
			buildNoteInstrument( note , true );
		}
	}

	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @param useRoughDraft Whether the timbre should be constructed as a rough draft.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note, boolean useRoughDraft) throws Throwable {
		
		final int core = 0;
		
		WaveForm inva = getEditPack2().processWave( new SineWaveform() );
		WaveForm invb = getEditPack3().processWave( new SineWaveform() );


		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );

		/*
		 * if( useInfraSonicHighPass() ) { inva = new HighPassFilter( inva ,
		 * 23.4 , 25 ); invb = new HighPassFilter( invb , 23.4 , 25 ); }
		 */

		WaveForm wave3a = inva;
		WaveForm wave3b = invb;

		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();

		NonClampedCoefficient primaryCoeff = buildPrimaryCoeff();

		NonClampedCoefficient secondaryCoeff = buildSecondaryCoeff();

		coefficients.add(wave3b);
		coefficientCoefficients.add(secondaryCoeff);
		parameterCoefficients.add(new ConstantNonClampedCoefficient(1.0));

		WaveForm wave3 = new AdditiveWaveForm(wave3a, primaryCoeff,
				coefficients, coefficientCoefficients, parameterCoefficients);
		
		wave3 = editPack1.processWave( wave3 );

		
		note.setWaveform(wave3);
		
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.6641604010025063 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		/* PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.5338345864661654 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.002506265664160401 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.002506265664160401 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.17293233082706766 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		/* PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		PhaseDistortionPacket[] pdccxk = { pdcxk };
		PhaseDistortionPacket[] pdccxl = { pdcxl };
		PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new Inverter(pdccxf);
		WaveForm invg = new Inverter(pdccxg);
		/* WaveForm invh = new Inverter(pdccxh);
		WaveForm invi = new Inverter(pdccxi);
		WaveForm invj = new Inverter(pdccxj);
		WaveForm invk = new Inverter(pdccxk);
		WaveForm invl = new Inverter(pdccxl);
		WaveForm invm = new Inverter(pdccxm); */
		primaryCoeff = new ConstantNonClampedCoefficient( -0.9113924050632911 );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.11392405063291139 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.08860759493670886 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.759493670886076 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.759493670886076 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.43037974683544306 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.4936708860759494 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
//		wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
//				coefficients , coefficientCoefficients , parameterCoefficients );
		
		
//		note.setWaveform( wave3 );
		
		
//		note.setRoughDraftWaveform(new SineWaveform());
		
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}

	/**
	 * Puts a phase distortion for the initial instrument "pluck" (or equivalent thereof) on top of the default timbre.
	 * @param wave0 The input default timbre
	 * @return The phase-distorted version of the timbre
	 */
	public static GWaveForm buildBassWaveFormB(GWaveForm wave0) {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, -8+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(1.0, -4+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, -2+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(3.0, -1+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(4.0, -0.5+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, 0.0+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(6.0, 0.0+8));
		bezAC.updateAll();

		GAnalogPhaseDistortionWaveForm wave = new GAnalogPhaseDistortionWaveForm(
				wave0, bezAC.genBez(new HashMap()));

		return (wave);
	}

	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame(TrackFrame tr) throws Throwable  {
		NoteDesc note1 = null;
		NoteDesc note2 = null;

		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
			for( final NoteDesc note : notes ) {
				note1 = note2;
				note2 = note;
				if (note1 != null) {
					processFirstNote(note1, note2);
				}
			}

			if (note2 != null) {
				processLastNote(note2);
			} else {
				processLastNote(note1);
			}
		}
	}

	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public SquirerAgentS1() {
		super();
		
		WaveForm inva = genWaveB();
		WaveForm invb = genWaveA();
		
		GWaveForm ina = new GRoughDraftWaveSwitch( inva.genWave( new HashMap() ) , new GSawtoothWaveform() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSawtoothWaveform() );
		
		ina = new GAppxBezWaveform( ina , "A" , getClass() );
		inb = new GAppxBezWaveform( inb , "B" , getClass() );
		
		ina = buildBassWaveFormB( ina );
		inb = buildBassWaveFormB( inb );
		
		getEditPack2().getWaveOut().performAssign( ina );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		getEditPack3().getWaveOut().performAssign( inb );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack3().getElem(), null, getEditPack3().getWaveOut());
		
		applyStdAmplRoll( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(2,
				NoteTable.STEPS_E) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(2,
				NoteTable.STEPS_F) );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fb );
	}

	@Override
	public void processTrack(InstrumentTrack track) throws Throwable {
		final int core = 0;

		track.updateTrackFramesComp( core );

		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		for( final TrackFrame tr : trackFrames ) {
			processTrackFrame(tr);
		}

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("MinDecayTimeBeats", minDecayTimeBeats);
		myv.setDouble("CutoffTimeWaves", cutoffTimeWaves);
		myv.setDouble("AttackTimeWaves", attackTimeWaves);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			minDecayTimeBeats = myv.getDouble("MinDecayTimeBeats");
			cutoffTimeWaves = myv.getDouble("CutoffTimeWaves");
			attackTimeWaves = myv.getDouble("AttackTimeWaves");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
