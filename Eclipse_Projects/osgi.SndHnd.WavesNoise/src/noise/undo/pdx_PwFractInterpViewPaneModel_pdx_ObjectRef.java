
/**************
* This source file was generated by JUndo compiler version 070812A.
* JUndo is a declarative object-oriented programming language with 
* functional, and intensional programming characteristics.
* See http://sourceforge.net/projects/jundo
* Do not modify this file directly.  Instead, modify the .JUndo file
* and recompile.  See the associated .JUndo file for license and
* copyright information.
* This File Generated : Tue Mar 22 19:20:04 MDT 2011
* From Input File : /Users/thorngreen/svngogl/ddt/trunk/osgi.SndHnd/src/labdaw/undo/PwFractInterpViewPaneModel.JUndo
**************/

package noise.undo;

public class pdx_PwFractInterpViewPaneModel_pdx_ObjectRef extends jundo.runtime.ExtObjectRef
	{
protected pdx_PwFractInterpViewPaneModel_pdx_ObjectRef( jundo.runtime.KeyRef Key )
{
super( Key );
}

public static noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef createOID_PwFractInterpViewPaneModel( jundo.runtime.ExtMilieuRef InMil )
{
jundo.runtime.ExtMilieuRef NewMil = jundo.runtime.Runtime.createOIDmilieu( InMil );
int NewID = NewMil.getMaxID();
jundo.runtime.KeyRef MyKey = jundo.runtime.Runtime.getKeyRef( NewID );
pdx_PwFractInterpViewPaneModel_pdx_ObjectRef NewObj = 
	new pdx_PwFractInterpViewPaneModel_pdx_ObjectRef( MyKey );
noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef ret = new noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef( NewObj , NewMil );
return( ret );
}
	public static noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef pdxm_new_PwFractInterpViewPaneModel( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  startParam , final double  endParam , final double  startLevel , final double  endLevel )
{
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef pdx_tmp_3162 = noise.undo.pdx_PwFractInterpViewPaneModel_pdx_ObjectRef.pdxm_allocate_PwFractInterpViewPaneModel( ( pdx_thismilieu ) );
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_ObjectRef pdx_tmp_3160 = (noise.undo.pdx_PwFractInterpViewPaneModel_pdx_ObjectRef)( pdx_tmp_3162.getObject() );
final jundo.runtime.ExtMilieuRef pdx_tmp_3161 = pdx_tmp_3162.getMilieu();
final jundo.runtime.ExtMilieuRef pdx_tmp_3480 = pdx_tmp_3160.pdxm_setStartParamNumber( pdx_tmp_3161 , ( startParam ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3640 = pdx_tmp_3160.pdxm_setEndParamNumber( pdx_tmp_3480 , ( endParam ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3800 = pdx_tmp_3160.pdxm_setStartLevel( pdx_tmp_3640 , ( startLevel ) );
final jundo.runtime.ExtMilieuRef pdx_tmp_3960 = pdx_tmp_3160.pdxm_setEndLevel( pdx_tmp_3800 , ( endLevel ) );
return( new noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef( pdx_tmp_3160 , ( pdx_tmp_3960 ) ) );
}
	public static noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef pdxm_allocate_PwFractInterpViewPaneModel( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef t1 = pdx_PwFractInterpViewPaneModel_pdx_ObjectRef.createOID_PwFractInterpViewPaneModel( pdx_thismilieu );
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef t2 = pdx_zero( t1.getMilieu() , (noise.undo.pdx_PwFractInterpViewPaneModel_pdx_ObjectRef)( t1.getObject() ) );

return( t2 );
}
	public static noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef pdx_zero( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_ObjectRef in )
{
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef tx = new noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef( in , pdx_thismilieu );
final jundo.runtime.ExtMilieuRef t0 = tx.getMilieu();
final jundo.runtime.ExtMilieuRef t1 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "startParamNumber" , t0 , 0 );
final jundo.runtime.ExtMilieuRef t2 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "endParamNumber" , t1 , 0 );
final jundo.runtime.ExtMilieuRef t3 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "endLevel" , t2 , 0 );
final jundo.runtime.ExtMilieuRef t4 = jundo.runtime.Runtime.asgDoubleObjectMember( tx.getObject() , "startLevel" , t3 , 0 );
final noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef z = new noise.undo.pdx_PwFractInterpViewPaneModel_pdx_PairRef( in , t4 );
return( z );
}
	public static void pdx_initClassMembers( )
{
}

	static
{
pdx_initClassMembers();
}

	public double pdx_ObjectMemberAccess_pdx_getstartParamNumber( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "startParamNumber" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgstartParamNumber( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "startParamNumber" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getendParamNumber( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "endParamNumber" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgendParamNumber( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "endParamNumber" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getendLevel( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "endLevel" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgendLevel( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "endLevel" , mil , val ) );
}
	public double pdx_ObjectMemberAccess_pdx_getstartLevel( final jundo.runtime.ExtMilieuRef mil )
{
return( jundo.runtime.Runtime.getDoubleObjectMember( this , "startLevel" , mil ) );
}
	public jundo.runtime.ExtMilieuRef pdx_ObjectMemberAccess_pdx_asgstartLevel( final jundo.runtime.ExtMilieuRef mil , final double val )
{
return( jundo.runtime.Runtime.asgDoubleObjectMember( this , "startLevel" , mil , val ) );
}
	public double pdxm_getEndParamNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_1100 = ( this ).pdx_ObjectMemberAccess_pdx_getendParamNumber( pdx_thismilieu );
return( pdx_tmp_1100 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setStartParamNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_860 = ( this ).pdx_ObjectMemberAccess_pdx_asgstartParamNumber( pdx_thismilieu , ( in ) );
return( pdx_tmp_860 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setStartLevel( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_1820 = ( this ).pdx_ObjectMemberAccess_pdx_asgstartLevel( pdx_thismilieu , ( in ) );
return( pdx_tmp_1820 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setEndLevel( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_2300 = ( this ).pdx_ObjectMemberAccess_pdx_asgendLevel( pdx_thismilieu , ( in ) );
return( pdx_tmp_2300 );
}
	public double pdxm_getStartParamNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_620 = ( this ).pdx_ObjectMemberAccess_pdx_getstartParamNumber( pdx_thismilieu );
return( pdx_tmp_620 );
}
	public double pdxm_getStartLevel( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_1580 = ( this ).pdx_ObjectMemberAccess_pdx_getstartLevel( pdx_thismilieu );
return( pdx_tmp_1580 );
}
	public jundo.runtime.ExtMilieuRef pdxm_setEndParamNumber( final jundo.runtime.ExtMilieuRef pdx_thismilieu , final double  in )
{
final jundo.runtime.ExtMilieuRef pdx_tmp_1340 = ( this ).pdx_ObjectMemberAccess_pdx_asgendParamNumber( pdx_thismilieu , ( in ) );
return( pdx_tmp_1340 );
}
	public double pdxm_getEndLevel( final jundo.runtime.ExtMilieuRef pdx_thismilieu )
{
final double pdx_tmp_2060 = ( this ).pdx_ObjectMemberAccess_pdx_getendLevel( pdx_thismilieu );
return( pdx_tmp_2060 );
}
	}

