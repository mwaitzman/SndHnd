package agents;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import waves.SawtoothWaveform;
import waves.TimeDiscretizedWaveForm;
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
import core.SongData;
import core.TrackFrame;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.AmplitudeModulationWaveForm;
import cwaves.AnalogPhaseDistortionWaveForm;
import cwaves.ClampedCoefficientRemodulator;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.FrequencyModulationWaveForm;
import cwaves.SineWaveform;
import cwaves.SquareWaveform;

/**
 * 
 * An accidentally discovered version of WhistleAgent that actually plays a low reverb sound effect.
 * 
 * Note: for this agent the apparent audible pitch of the generated note is roughly one octave down from the actual oscillatory frequency of the waveform.
 * 
 * Timbre is generated by modulating a sine wave with a square wave and a sawtooth wave.
 * 
 * @author tgreen
 *
 */
public class WhistleBAgent extends IntelligentAgent implements Externalizable {
	
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
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note) throws Throwable {

		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );

		/*
		 * if( useInfraSonicHighPass() ) { inva = new HighPassFilter( inva ,
		 * 23.4 , 25 ); invb = new HighPassFilter( invb , 23.4 , 25 ); }
		 */
		
		currentNote = note;
		
		final int core = 0;
		
		WaveForm wi = getEditPack2().processWave( new SineWaveform() );
		
		FrequencyModulationWaveForm frm = new FrequencyModulationWaveForm( new SineWaveform() ,
				new ConstantNonClampedCoefficient( 6.5 / ( note.getFreqAndBend().getBaseFreq() ) ) );
		
		AmplitudeModulationWaveForm am = new AmplitudeModulationWaveForm( wi , frm );
		
		WaveForm wii = new TimeDiscretizedWaveForm( am , 4000.0 / 350.0 );

		WaveForm wave3a = buildBassWaveFormB( wii );

		WaveForm wave3 = wave3a;
		
		
		wave3 = editPack1.processWave( wave3 );

		
		note.setWaveform(wave3);
		
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}

	/**
	 * Puts a phase distortion for the initial activation of the whistle's resonance top of the default timbre.
	 * @param wave0 The input default timbre
	 * @return The phase-distorted version of the timbre
	 */
	public static WaveForm buildBassWaveFormB(WaveForm wave0) { // !!!!!!!!!!!! convert to GWaveForm !!!!!!!!!!!!!!!!!!!!!!!!!!!
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, -8+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(1.0, -4+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, -2+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(3.0, -1+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(4.0, -0.5+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, 0.0+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(6.0, 0.0+8));
		bezAC.updateAll();

		AnalogPhaseDistortionWaveForm wave = new AnalogPhaseDistortionWaveForm(
				wave0, bezAC);

		return (wave);
	}

	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame(TrackFrame tr) throws Throwable {
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
	public WhistleBAgent() {
		super();
		
		WaveForm sw = new SineWaveform();
		
		ArrayList<NonClampedCoefficient> parmCoeff = new ArrayList<NonClampedCoefficient>();
		parmCoeff.add( new ConstantNonClampedCoefficient( 2.0 ) );
		parmCoeff.add( new ConstantNonClampedCoefficient( 3.0 ) );
		parmCoeff.add( new ConstantNonClampedCoefficient( 0.5 ) );
		parmCoeff.add( new ConstantNonClampedCoefficient( 0.25 ) );
		
		ArrayList<NonClampedCoefficient> coeffCoeff = new ArrayList<NonClampedCoefficient>();
		coeffCoeff.add( new ConstantNonClampedCoefficient( 1.0 / 6.5 ) );
		coeffCoeff.add( new ConstantNonClampedCoefficient( 1.0 / 6.5 ) );
		coeffCoeff.add( new ConstantNonClampedCoefficient( 2.0 / 6.5 ) );
		coeffCoeff.add( new ConstantNonClampedCoefficient( 0.5 / 6.5 ) );
		
		ArrayList<NonClampedCoefficient> coeff = new ArrayList<NonClampedCoefficient>();
		coeff.add( sw );
		coeff.add( sw );
		coeff.add( new SawtoothWaveform() );
		coeff.add( new SquareWaveform( 0.25 ) );
		
		AdditiveWaveForm wi = new AdditiveWaveForm( sw , 
				new ConstantNonClampedCoefficient( 1.0 / 6.5 ) , 
				coeff , coeffCoeff , parmCoeff );
		
		getEditPack2().getWaveOut().performAssign( wi.genWave( new HashMap() ) );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		applyStdAmplRoll( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( 350 );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(4,
				NoteTable.STEPS_A) );
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
