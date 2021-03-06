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
import waves.GRoughDraftWaveSwitch;
import waves.GSawtoothWaveform;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
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
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GAnalogPhaseDistortionWaveForm;
import cwaves.SineWaveform;
import cwaves.SinglePacketInverter;


/**
 * 
 * Percussion agent created to emulate the playing of a cryst drum using Xtal and inverters.  This is a work in progress.
 * 
 * Note: like (some) real percussive instruments, BassCrystDrumAgent plays on a particular key.  That is to say, the key of the percussion could match/mismatch the key of the rest of the song.
 * 
 * @author tgreen
 *
 */
public class BassCrystDrumAgent extends IntelligentAgent implements Externalizable {
	
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

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalPrim(0.75)));
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

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalSec(0.25)));
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
	 * Builds the secondary timbre for the agwnt.
	 * @return The secondary timbre for the agent.
	 */
	protected WaveForm genWaveHi()
	{
		final double maxDivisor = 4.5;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.6641604010025063 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		//PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
		//		new SineWaveform(), 1.0, 0.7944862155388471 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.3383458646616541 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.002506265664160401 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.47869674185463656 );
		/* PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		//PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		PhaseDistortionPacket[] pdccxk = { pdcxk };
		PhaseDistortionPacket[] pdccxl = { pdcxl };
		/* PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new SinglePacketInverter(pdcxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		//WaveForm invh = new SinglePacketInverter(pdccxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		WaveForm invj = new SinglePacketInverter(pdcxj);
		WaveForm invk = new SinglePacketInverter(pdcxk);
		WaveForm invl = new SinglePacketInverter(pdcxl);
		/* WaveForm invm = new SinglePacketInverter(pdccxm); */
		ConstantNonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( -0.9113924050632911 );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.11392405063291139 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		//coefficients.add( invh );
		//coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.0759493670886076 ));
		//parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		//coefficients.add( invi );
		//coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 ));
		//parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.3291139240506329 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.012658227848101266 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( /* invf */ invi , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		return( wave3 );
	}
	
	
	/**
	 * Builds the primary timbre for the agwnt.
	 * @return The primary timbre for the agent.
	 */
	protected WaveForm genWaveLo()
	{
		final double maxDivisor = 3.62;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.5789473684210527 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.20050125313283207 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.48120300751879697 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.22807017543859648 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41353383458646614 );
		/* PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.47869674185463656 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		//PhaseDistortionPacket[] pdccxk = { pdcxk };
		//PhaseDistortionPacket[] pdccxl = { pdcxl };
		//PhaseDistortionPacket[] pdccxm = { pdcxm };
		WaveForm invf = new SinglePacketInverter(pdcxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		WaveForm invh = new SinglePacketInverter(pdcxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		WaveForm invj = new SinglePacketInverter(pdcxj);
		//WaveForm invk = new SinglePacketInverter(pdccxk);
		//WaveForm invl = new SinglePacketInverter(pdccxl);
		//WaveForm invm = new SinglePacketInverter(pdccxm);
		ConstantNonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( -0.9620253164556962 / maxDivisor );
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
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.3291139240506329 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( ( 0.02531645569620253 + 0.11392405063291139 ) ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.012658227848101266 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( invf /* invi */ , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		
		return( wave3 );
	}
	

	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note) throws Throwable {
		
		currentNote = note;
		
		WaveForm inva = getEditPack2().processWave( new SineWaveform() );
		WaveForm invb = getEditPack3().processWave( new SineWaveform() );
		
		final int core = 0;

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
		
		double maxDivisor = 3.5;
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.9774436090225563 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.8546365914786967 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.007518796992481203 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		/* PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41353383458646614 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.41854636591478694 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.47869674185463656 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		//PhaseDistortionPacket[] pdccxj = { pdcxj };
		//PhaseDistortionPacket[] pdccxk = { pdcxk };
		//PhaseDistortionPacket[] pdccxl = { pdcxl };
		//PhaseDistortionPacket[] pdccxm = { pdcxm };
		WaveForm invf = new SinglePacketInverter(pdcxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		WaveForm invh = new SinglePacketInverter(pdcxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		//WaveForm invj = new SinglePacketInverter(pdccxj);
		//WaveForm invk = new SinglePacketInverter(pdccxk);
		//WaveForm invl = new SinglePacketInverter(pdccxl);
		//WaveForm invm = new SinglePacketInverter(pdccxm);
		primaryCoeff = new ConstantNonClampedCoefficient( 1.0 / maxDivisor );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.8481012658227848 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.9240506329113924 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.012658227848101266 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.3291139240506329 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( ( 0.02531645569620253 + 0.11392405063291139 ) ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.012658227848101266 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		
		note.setWaveform( wave3 );
		
		
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
	 * Produces a representation of how a musician would play an instrument for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote( NoteDesc note1 , NoteDesc note2 ) throws Throwable
	{
		final int core = 0;
		note1.setActualEndBeatNumberValidated( note1.getEndBeatNumber() , note2.getStartBeatNumber() );
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );
		buildNoteInstrument( note1 );
		SongData.buildBendInterpPoints(note1,note2,10,minDecayTimeBeats,false,core);
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote( NoteDesc note1 ) throws Throwable
	{
		final int core = 0;
		note1.setActualEndBeatNumberValidated( note1.getEndBeatNumber() , 1E+40 );
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );
		buildNoteInstrument( note1 );
		SongData.buildBendInterpPoints(note1,null,10,minDecayTimeBeats,false,core);
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame( TrackFrame tr ) throws Throwable
	{
		NoteDesc note1 = null;
		NoteDesc note2 = null;
		
		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
		for( final NoteDesc note : notes )
		{
			note1 = note2;
			note2 = note;
			if( note1 != null )
			{
				processFirstNote( note1 , note2 );
			}
		}
		
		if( note2 != null )
		{
			processLastNote( note2 );
		}
		else
		{
			processLastNote( note1 );
		}
		}
	}

	
	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public BassCrystDrumAgent() {
		super();
		
		WaveForm inva = genWaveLo(); // new SinglePacketInverter(pdccxa);
		WaveForm invb = genWaveHi();
		
		GWaveForm ina = new GRoughDraftWaveSwitch( inva.genWave( new HashMap() ) , new GSawtoothWaveform() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSawtoothWaveform() );
		
		ina = buildBassWaveFormB(ina);
		inb = buildBassWaveFormB(inb);
		
		getEditPack2().getWaveOut().performAssign( ina );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		getEditPack3().getWaveOut().performAssign( inb );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack3().getElem(), null, getEditPack3().getWaveOut());
		
		applyStdAmplRoll( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(1,
				NoteTable.STEPS_F) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(1,
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
