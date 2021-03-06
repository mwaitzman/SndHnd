
/**************
* This source file was generated by JUndo compiler version 070812A.
* JUndo is a declarative object-oriented programming language with 
* functional, and intensional programming characteristics.
* See http://sourceforge.net/projects/jundo
* Do not modify this file directly.  Instead, modify the .JUndo file
* and recompile.  See the associated .JUndo file for license and
* copyright information.
* This File Generated : Sun Jan 27 08:59:37 MST 2008
* From Input File : NoteViewPaneModel.JUndo
**************/

package labdaw.undo;

public class pdx_NoteViewPaneModel_pdx_ObjectRef extends jundo.runtime.ExtObjectRef
	{
protected pdx_NoteViewPaneModel_pdx_ObjectRef( jundo.runtime.KeyRef Key )
{
super( Key );
}

public static labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef createOID_NoteViewPaneModel( jundo.runtime.ExtMilieuRef InMil )
{
jundo.runtime.ExtMilieuRef NewMil = jundo.runtime.Runtime.createOIDmilieu( InMil );
int NewID = NewMil.getMaxID();
jundo.runtime.KeyRef MyKey = jundo.runtime.Runtime.getKeyRef( NewID );
pdx_NoteViewPaneModel_pdx_ObjectRef NewObj = 
	new pdx_NoteViewPaneModel_pdx_ObjectRef( MyKey );
labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef ret = new labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef( NewObj , NewMil );
return( ret );
}
	public static labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef pdxm_new_NoteViewPaneModel( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  startBeat , final double  endBeat , final double  startFreq , final double  endFreq )
{
final labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef pdx_tmp_3162 = labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef.pdxm_allocate_NoteViewPaneModel( ( pdx_thismilieu ) );
final labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef pdx_tmp_3160 = (labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef)( pdx_tmp_3162.getObject() );
final jundo.runtime.ExtMilieuRef pdx_tmp_3161 = pdx_tmp_3162.getMilieu();
final jundo.runtime.ExtMilieuRef pdx_tmp_3480 = pdx_tmp_3160.pdxm_setStartBeatNumber( pdx_tmp_3161 , ( startBeat ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3640 = pdx_tmp_3160.pdxm_setEndBeatNumber( pdx_tmp_3480 , ( endBeat ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3800 = pdx_tmp_3160.pdxm_setStartFrequency( pdx_tmp_3640 , ( startFreq ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3960 = pdx_tmp_3160.pdxm_setEndFrequency( pdx_tmp_3800 , ( endFreq ) );
return( new labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef( pdx_tmp_3160 , ( pdx_tmp_3960 ) ) );
}
	public static labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef pdxm_allocate_NoteViewPaneModel( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef t1 = pdx_NoteViewPaneModel_pdx_ObjectRef.createOID_NoteViewPaneModel( pdx_thismilieu );
final labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef t2 = pdx_zero( t1.getMilieu() , (labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef)( t1.getObject() ) );

return( t2 );
}
	public static labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef pdx_zero( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final labdaw.undo.pdx_NoteViewPaneModel_pdx_ObjectRef in )
{
final labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef tx = new labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef( in , pdx_thismilieu );
final jundo.runtime.ExtMilieuRef t0 = tx.getMilieu();
final jundo.runtime.ExtMilieuRef t1 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "startFrequency" , t0 , 0 );
final jundo.runtime.ExtMilieuRef t2 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "endBeatNumber" , t1 , 0 );
final jundo.runtime.ExtMilieuRef t3 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "endFrequency" , t2 , 0 );
final jundo.runtime.ExtMilieuRef t4 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "startBeatNumber" , t3 , 0 );
final labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef z = new labdaw.undo.pdx_NoteViewPaneModel_pdx_PairRef( in , t4 );
return( z );
}
	public static void pdx_initClassMembers( )
{
}

	static
{
pdx_initClassMembers();
}

	public double pdx_ObjectMemberAccess_pdx_getstartFrequency( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "startFrequency" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgstartFrequency( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "startFrequency" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getendBeatNumber( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "endBeatNumber" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgendBeatNumber( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "endBeatNumber" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getendFrequency( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "endFrequency" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgendFrequency( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "endFrequency" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getstartBeatNumber( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "startBeatNumber" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgstartBeatNumber( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "startBeatNumber" , mil , val ) );
}
	public double pdxm_getStartFrequency( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_1580 = ( this ).pdx_ObjectMemberAccess_pdx_getstartFrequency( pdx_thismilieu );
return( pdx_tmp_1580 );
}
	public double pdxm_getEndFrequency( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_2060 = ( this ).pdx_ObjectMemberAccess_pdx_getendFrequency( pdx_thismilieu );
return( pdx_tmp_2060 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setStartFrequency( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_1820 = ( this ).pdx_ObjectMemberAccess_pdx_asgstartFrequency( pdx_thismilieu , ( in ) );
return( pdx_tmp_1820 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setStartBeatNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_860 = ( this ).pdx_ObjectMemberAccess_pdx_asgstartBeatNumber( pdx_thismilieu , ( in ) );
return( pdx_tmp_860 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setEndBeatNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_1340 = ( this ).pdx_ObjectMemberAccess_pdx_asgendBeatNumber( pdx_thismilieu , ( in ) );
return( pdx_tmp_1340 );
}
	public double pdxm_getStartBeatNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_620 = ( this ).pdx_ObjectMemberAccess_pdx_getstartBeatNumber( pdx_thismilieu );
return( pdx_tmp_620 );
}
	public double pdxm_getEndBeatNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_1100 = ( this ).pdx_ObjectMemberAccess_pdx_getendBeatNumber( pdx_thismilieu );
return( pdx_tmp_1100 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setEndFrequency( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_2300 = ( this ).pdx_ObjectMemberAccess_pdx_asgendFrequency( pdx_thismilieu , ( in ) );
return( pdx_tmp_2300 );
}
	}

