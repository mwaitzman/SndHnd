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
import waves.GHarmonicDistortionWaveForm;
import waves.GRoughDraftSkipDistortion;
import waves.GRoughDraftWaveSwitch;
import waves.GSawtoothWaveform;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
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
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.AmplitudeModulationNonClampedCoeff;
import cwaves.AnalogPhaseDistortionWaveForm;
import cwaves.ClampedCoefficientRemodulator;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.Inverter;
import cwaves.SineWaveform;


/**
 * 
 * Early prototype for an agent emulating the playing of string 2 (the A string) on a bass guitar. 
 * 
 * Timbre is based on a combination of a single inverter and a sine wave.
 * 
 * It is also true that actual instrument playing doesn't usually produce the default vibrato generated by this agent, but this can be turned off by changing the GUI parameter.
 * 
 * @author thorngreen
 *
 */
public class BassGuitarPlayingAgent extends IntelligentAgent implements Externalizable
	 {
	
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
	protected double cutoffTimeWaves = 0.75;

	/**
	 * The duration of the initial attack of a note in waves.
	 */
	protected double attackTimeWaves = 1.0 /* 5.0 */;
	
	/**
	 * Sets the duration of the initial attack of a note in waves.
	 * @param in The duration of the initial attack of a note in waves.
	 */
	public void setAttackTimeWaves( double in )
	{
		attackTimeWaves = in;
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
		double decayTimeBeats = note1.getEndBeatNumber() - note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max( minDecayTimeBeats , decayTimeBeats );
		note1.setActualEndBeatNumberValidated( note1.getStartBeatNumber() + decayTimeBeatsPlay , note2.getStartBeatNumber() );
		double decayTimeBeatsFinal = note1.getActualEndBeatNumber() - note1.getStartBeatNumber();
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );
		
		double ratio = decayTimeBeatsFinal / decayTimeBeatsPlay;
		ClampedCoefficient ci = note1.getNoteEnvelope( core );
		if (ratio < 0.9999999999)
		{
			/* ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			note1.setWaveEnvelope(buildNoteAttack()); */
			
			ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			double endWaveNumber = note1.getEndWaveNumber( core );
			double startWaveNumber = endWaveNumber - cutoffTimeWaves;
			NonClampedCoefficient noteAttack = buildNoteAttack();
			NonClampedCoefficient noteDecay = buildNoteDecay(note1,
					startWaveNumber, endWaveNumber);
			NonClampedCoefficient finalEnv = new AmplitudeModulationNonClampedCoeff(
					noteAttack, noteDecay);
			note1.setWaveEnvelope(finalEnv);
		} else {
			note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
			note1.setWaveEnvelope(buildNoteAttack());
		}
		
		if( note1.getWaveform( core ) == null )
		{
			buildNoteInstrument( note1 , false );
		
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
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote( NoteDesc note1 ) throws Throwable
	{
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber() - note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max( minDecayTimeBeats , decayTimeBeats );
		note1.setActualEndBeatNumberValidated( note1.getStartBeatNumber() + decayTimeBeatsPlay, 1E+40 );
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope( buildNoteAttack() );
		if( note1.getWaveform( core ) == null )
		{
			buildNoteInstrument( note1 , false );
		
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
	}
	
	/**
	 * Builds the final decay for a note.
	 * @param note The note for which to build the decay.
	 * @param startWaveNumber The starting wave number of the note.
	 * @param endWaveNumber The ending wave number of the note.
	 * @return The decay coefficient.
	 */
	protected NonClampedCoefficient buildNoteDecay( NoteDesc note , double startWaveNumber , double endWaveNumber )
	{
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData.buildGradualDecayBezier( 20 , startWaveNumber , endWaveNumber );
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat( bezDrive );
		return( bzC );
	}
	
	/**
	 * Builds the initial attack for a note.
	 * @return The attack.
	 */
	protected NonClampedCoefficient buildNoteAttack( )
	{
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData.buildGradualAttackBezier( 20 , 0.0 , attackTimeWaves );
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat( bezDrive );
		return( bzC );
	}
	
	
	/**
	 * Builds a coefficient for the primary timbre.
	 * @return The coefficient for the primary timbre.
	 */
	protected NonClampedCoefficient buildPrimaryCoeff()
	{
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/* bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.86 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 , 0.79 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 13.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 15.0 , 0.75 ) ); */
		
		/* bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.82 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 , 0.86 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 21.0 , 0.79 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 25.0 , 0.75 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 , 0.75 ) ); */
		
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.92 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.92 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.93 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 18.0 , 0.95 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 36.0 , 0.93 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 52.0 , 0.92 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 , 0.92 ) );
		
		bezAC.updateAll();
		
		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat( bezAC );
		
		return( coeff );
	}
	
	
	/**
	 * Builds a coefficient for the secondary timbre.
	 * @return The coefficient for the secondary timbre.
	 */
	protected NonClampedCoefficient buildSecondaryCoeff()
	{
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/* bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.09 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 , 0.23 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 13.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 15.0 , 0.25 ) ); */
		
		/* bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.15 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 , 0.09 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 21.0 , 0.23 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 25.0 , 0.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 , 0.25 ) ); */
		
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.03 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.03 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 7.5 , 0.02 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 18.0 , 0.01 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 36.0 , 0.02 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 52.0 , 0.03 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 , 0.03 ) );
		
		bezAC.updateAll();
		
		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat( bezAC );
		
		return( coeff );
	}
	
	
	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @param shortRecover Whether to use a note attack that recovers more quickly.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note, boolean shortRecover) throws Throwable
	{
		currentNote = note;
		buildNoteInstrument(note, shortRecover, false);
		if( SongData.roughDraftMode == SongData.ROUGH_DRAFT_MODE_BEZ_APPROX )
		{
			buildNoteInstrument( note , shortRecover, true );
		}
	}
	
	
	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @param shortRecover Whether to use a note attack that recovers more quickly.
	 * @param useRoughDraft Whether the timbre should be constructed as a rough draft.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument( NoteDesc note , boolean shortRecover , boolean useRoughDraft ) throws Throwable {
		
		final int core = 0;
		
		WaveForm inva = getEditPack2().processWave( new SineWaveform() );
		WaveForm invb = getEditPack3().processWave( new SineWaveform() );
		

		WaveForm wave3a = buildBassWaveFormB( inva , shortRecover ); // !!!!!!!!!!!!!!!!!!!!!! more params in edit pack !!!!!!!!!!!
		WaveForm wave3b = buildBassWaveFormB( invb , shortRecover ); // !!!!!!!!!!!!!!!!!!!!!! more params in edit pack !!!!!!!!!!!

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
		
		
		wave3 = editPack1.processWave( wave3 );
		
		note.setWaveform( wave3 );
	
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}
	
	
	
	/**
	 * Puts a phase distortion for the initial instrument "pluck" (or equivalent thereof) on top of the default timbre.
	 * @param wave0 The input default timbre
	 * @param shortRecover Whether the distortion should recover in a shorter period of time.
	 * @return The phase-distorted version of the timbre
	 */
	public static WaveForm buildBassWaveFormB( WaveForm wave0  , boolean shortRecover )
	{
		double val = shortRecover ? -0.05 : -0.4;
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , val + 1.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , 0.0 + 1.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 4.0 , 0.0 + 1.0 ) );
		bezAC.updateAll();
		
		AnalogPhaseDistortionWaveForm wave = new AnalogPhaseDistortionWaveForm(
				wave0, bezAC);
		
		return( wave );
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
			note.setWaveform( null );
		}
		
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
	public BassGuitarPlayingAgent() {
		super();
		
		WaveForm inva = new SineWaveform();
		PhaseDistortionPacket pdcxb = new PhaseDistortionPacket( new SineWaveform( ) , 1.0 ,  /* 0.3 */ 0.4 );
		PhaseDistortionPacket[] pdccxb = { pdcxb };
		WaveForm invb = new Inverter( pdccxb );
		
		GWaveForm ina = inva.genWave( new HashMap() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSawtoothWaveform() );
		
		ina = new GAppxBezWaveform( ina , "A" , getClass() );
		inb = new GAppxBezWaveform( inb , "B" , getClass() );
		
		getEditPack2().getWaveOut().performAssign( ina );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		getEditPack3().getWaveOut().performAssign( inb );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack3().getElem(), null, getEditPack3().getWaveOut());
		
		GHarmonicDistortionWaveForm distort = new GHarmonicDistortionWaveForm();
		distort.setFirstHarmonicDistortion( 0.25 );
		distort.setMaxHarmonicNum( 11 );
		distort.setFirstSubHarmonicDistortion( 0.25 );
		distort.setMaxSubHarmonicNum( -1 );
		distort.performAssign( getEditPack1().getWaveIn() );
		
		GWaveForm distortA = new GRoughDraftSkipDistortion( distort , getEditPack1().getWaveIn() );
		
		applyStdAmplRoll( distortA , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(1,
				NoteTable.STEPS_A) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(1,
				NoteTable.STEPS_B) );
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
			processTrackFrame( tr );
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
