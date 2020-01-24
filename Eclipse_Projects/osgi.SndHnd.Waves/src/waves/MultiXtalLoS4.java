





//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package waves;

import java.util.ArrayList;

import core.WaveForm;



/**
 * 
 * Defines Xtal timbre for class BassLoExprAgentS4.
 * 
 * @author tgreen
 *
 */
public class MultiXtalLoS4 {
	

	
	/**
	 * Gets the waveform for the primary timbre.
	 * @return The waveform for the primary timbre.
	 */
	public static WaveForm builtMultiXtalLoS4A2()
	{
		ArrayList<XtalWaveform> r = new ArrayList<XtalWaveform>();
		

		final double cnst = 0.0;
		
		
		
		{
			double[] fit = { 0.02 , 0.0 , 0.0 , 1.0 , 1.0 , 0.0 , 0.022287834739357786 , -0.040330478220006946 , 0.9411278864771148 , 7.9478732827047525 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.19742469595152504 , 0.00784867013203379 , 0.13133374556887856 , 0.9411278864771148 , 7.9478732827047525 , 6.283185307179586 , -0.006674210271114841 , 0.21933432311250783 , 0.08636518149453704 , 5.713728516029837 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.3120870688371131 , 0.197163180868487 , -0.12513891451391584 , 0.08636518149453704 , 5.713728516029837 , 6.283185307179586 , 0.08969584953186666 , 0.004033859388253242 , -4.834709948848465 , -15.419412017944847 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.34320339213099954 , -0.034375899873099844 , -0.005967819169064125 , -4.834709948848465 , -15.419412017944847 , 6.283185307179586 , -0.13286913664019467 , -0.039177919464008594 , 5.852448416253723 , 4.135959421531158 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.385663228077324 , -0.11860321078364841 , -0.6493520889318111 , 5.852448416253723 , 4.135959421531158 , 6.283185307179586 , -0.10667978556145451 , -0.6201488624097845 , 1.3290956804811254 , 4.155955582132111 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.45250837191524396 , 1.0996553304174956 , 0.004784894650808393 , 1.3290956804811254 , 4.155955582132111 , 6.283185307179586 , 1.0928250561240993 , 0.0032311472084754207 , -1.2794774168481609 , 4.046141702427445 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.5173541883948397 , -0.05231487534555726 , 0.6467480723999172 , -1.2794774168481609 , 4.046141702427445 , 6.283185307179586 , -0.048484218279932705 , 0.6756722083811189 , -6.262528794876647 , 4.117434694258771 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.5567790731769625 , -0.1274153638643177 , 0.06620697939019769 , -6.262528794876647 , 4.117434694258771 , 6.283185307179586 , -0.03091321162784451 , 0.025319908523934596 , 4.445480948067849 , 29.201604646045503 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.5813593074264256 , 0.06162931293843263 , 0.05005253660363836 , 4.445480948067849 , 29.201604646045503 , 6.283185307179586 , 0.20766526514378678 , 0.00859797744865842 , 1.5341213043402986 , 3.6148189737908636 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.6561112173483125 , -0.07160299310825746 , 0.4211902858332181 , 1.5341213043402986 , 3.6148189737908636 , 6.283185307179586 , -0.13125170989428867 , 0.2808008433421251 , -2.001238538932426 , 0.18514151430500847 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 0.7566432444181387 , -0.04607985169865293 , 0.07445594838057719 , -2.001238538932426 , 0.18514151430500847 , 6.283185307179586 , 8.613295224492869E-4 , 0.0767583212999131 , -0.27840379741404847 , 0.7387458003847732 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
	
		{
			double[] fit = { 1.0876512366013817 , -0.04297701679972488 , 0.0019604684336903167 , -0.27840379741404847 , 0.7387458003847732 , 6.283185307179586 , 0.020165721452700514 , 0.05347193414718803 , -0.0024020507440813592 , 0.12625095545917053 , 6.283185307179586 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
		{
			double[] fit = { 19.999999999999996 , -0.03760414940551836 , -0.020767111620014947 , -0.0024020507440813592 , 0.12625095545917053 , 6.283185307179586 , 0.0 , 0.0 , 1.0 , 1.0 , 0.0 } ;
			XtalWaveform xt = new XtalWaveform( fit , cnst );
			r.add( xt );
		}
			 
		 

		
		
		WaveForm wave3 = new MMXtalWaveForm( new MultiXtalWaveform( r ) );
		
		//PhaseDistortionPacket pdcxf2 = new PhaseDistortionPacket(
		//		wave3 , 1.0,  0.032659147869674186 );
		//PhaseDistortionPacket[] pdccxf2 = { pdcxf2 };
		//wave3 = new Inverter(pdccxf2);
		
		//wave3 = new SpreadWaveform( wave3 , (int) ( 20 ) , ( (int) ( 600 * 300 ) ) );
		
	    // wave3 = (WaveForm)( r.get( 6 ) );
		
		// ...
		
		return( wave3 );
		
	}
	
	
	
	
	
	
	

	
	

	
	
	
	
	

	/**
	 * Gets the waveform for the secondary timbre.
	 * @return The waveform for the secondary timbre.
	 */
	public static WaveForm builtMultiXtalLoS4B2() 
	{
		ArrayList<XtalWaveform> r = new ArrayList<XtalWaveform>();
		

		final double cnst = 0.0;
	   
	       
	        
	       {
				double[] fit =   { 0.02 , 0.0 , 0.0 , 1.0 , 1.0 , 0.0 , -0.09010480664969509 , -0.05314128266610089 , -1.2451859066283508 , 3.712012334630666 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.1259735959598929 , 0.04522670983940371 , -0.00623870674195472 , -1.2451859066283508 , 3.712012334630666 , 6.283185307179586 , -0.020566980711946226 , -0.0028031062284295406 , 5.632300576314884 , 3.649533568721161 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.19320600245775757 , 0.023789028250704884 , -0.22284964728498105 , 5.632300576314884 , 3.649533568721161 , 6.283185307179586 , 0.07174111742000334 , -0.20757140769049826 , -7.446684319352404 , 2.306245224731094 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.25145897670039036 , 0.013284898622656626 , -0.005521626337144118 , -7.446684319352404 , 2.306245224731094 , 6.283185307179586 , -0.059924343524128086 , -0.007419229097087148 , 0.7903371224320043 , 3.8800047902926567 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.37256225295887113 , 0.10993447670883082 , -0.00727267249112424 , 0.7903371224320043 , 3.8800047902926567 , 6.283185307179586 , -0.002051085347316882 , -0.00676034570278121 , 19.19519917631213 , -6.693797791917573 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.3992248927790376 , -0.1739500842190705 , -0.027151137774652265 , 19.19519917631213 , -6.693797791917573 , 6.283185307179586 , -0.13893582515711764 , -0.10958477699157844 , 0.9198541339519428 , 4.176119945659871 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.506299042846633 , 0.31021060537716555 , 0.10826971896073807 , 0.9198541339519428 , 4.176119945659871 , 6.283185307179586 , 0.3068452306705598 , 0.045653532832324574 , -5.276658726242349 , 6.838419766065077 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.622964632971103 , 0.0028091397069669234 , -0.005843857906231971 , -5.276658726242349 , 6.838419766065077 , 6.283185307179586 , -0.019765735755944633 , 0.005187458870393142 , 4.55931651990636 , 4.111004043347771 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.7030662079067903 , 0.04850395938428896 , -0.1968529955340777 , 4.55931651990636 , 4.111004043347771 , 6.283185307179586 , -0.06500808778230939 , -0.10670005298819947 , -3.995941048316785 , 12.074118339288791 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.769831804389936 , -0.022931941378360608 , 0.004517121619117901 , -3.995941048316785 , 12.074118339288791 , 6.283185307179586 , -0.12034798875528921 , -0.03887670298273274 , 1.035847289442183 , 4.181198165483494 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.8576303954118013 , 0.19406726800260357 , -0.11177622923975485 , 1.035847289442183 , 4.181198165483494 , 6.283185307179586 , 0.16172754809353138 , 0.004195534306765491 , -17.558027967338592 , -10.195246379775709 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.8801510272063838 , 0.0020701037853603286 , -0.013327722092292714 , -17.558027967338592 , -10.195246379775709 , 6.283185307179586 , -0.02895662065624875 , -8.861390383963168E-4 , 22.565544622103896 , 4.164750798196522 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.8916321465907109 , -0.13952991004672938 , -0.04795302636036548 , 22.565544622103896 , 4.164750798196522 , 6.283185307179586 , -0.14980558911308303 , -0.05140385053971412 , 5.34920288458064 , 4.061612274955454 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 0.9350258478588308 , -0.0901936665630421 , -0.6749290665522669 , 5.34920288458064 , 4.061612274955454 , 6.283185307179586 , -0.08731860369812744 , -0.6523294545704855 , 1.1995565111103617 , 4.052720193158162 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.00194153028777 , 1.0898279378893352 , 8.549532740676493E-5 , 1.1995565111103617 , 4.052720193158162 , 6.283185307179586 , 1.088559720863339 , 2.5812457720835114E-4 , -1.2833680595355297 , 4.049307103447189 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.0690178760666291 , -0.08595241540747496 , 0.6279433947683721 , -1.2833680595355297 , 4.049307103447189 , 6.283185307179586 , -0.09038932454292471 , 0.643657304733739 , -5.46417000084064 , 3.9688883603676275 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.1114003289534238 , -0.14118349482961068 , 0.0554717833711858 , -5.46417000084064 , 3.9688883603676275 , 6.283185307179586 , -0.04302581492550081 , 0.0072141556023694485 , 1.9784756656766556 , -14.134523053431453 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.1455227779038812 , 0.06657529358704059 , -0.003661416485694618 , 1.9784756656766556 , -14.134523053431453 , 6.283185307179586 , 0.1910765901725532 , 0.11978344860064248 , -1.3467070175863263 , 4.052131477921608 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.2381544879618018 , -0.10043050322036745 , 0.022796641449302903 , -1.3467070175863263 , 4.052131477921608 , 6.283185307179586 , -0.012455921242415176 , 0.013410671254871612 , 5.465779525136037 , -5.514318494029286 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.2928155354352078 , 0.10895019488168088 , 0.04936585583949481 , 5.465779525136037 , -5.514318494029286 , 6.283185307179586 , 0.05517321284059837 , 0.20239698132787318 , -4.226181877840761 , 3.924064966943125 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.3734187710740724 , -0.024395271767117723 , -0.0036968258133646733 , -4.226181877840761 , 3.924064966943125 , 6.283185307179586 , 0.0027382282593408744 , 0.011658982108627597 , 8.216501191713366 , 0.6507636498982725 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.4278695503005736 , 0.0016004739686440103 , 0.19912951205097526 , 8.216501191713366 , 0.6507636498982725 , 6.283185307179586 , -0.08506562675295651 , 0.20583860909980725 , -1.3609408406397616 , 5.451839595960129 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 1.6184434818584301 , -0.025958459674386935 , 0.03509904190846317 , -1.3609408406397616 , 5.451839595960129 , 6.283185307179586 , 0.002516263782118358 , -0.00995597775331338 , -0.022973726833277825 , 2.7723287594152675 , 6.283185307179586 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	       
			{
				double[] fit =   { 19.999999999999996 , -4.010128398192126E-6 , -7.230863582706744E-4 , -0.022973726833277825 , 2.7723287594152675 , 6.283185307179586 , 0.0 , 0.0 , 1.0 , 1.0 , 0.0 } ;
	  			XtalWaveform xt = new XtalWaveform( fit , cnst );
				r.add( xt );
			}
	      
		 
		
		
		
		
		WaveForm wave3 = new MMXtalWaveForm( new MultiXtalWaveform( r ) );
		
		//PhaseDistortionPacket pdcxf2 = new PhaseDistortionPacket(
		//		wave3 , 1.0,  0.032659147869674186 );
		//PhaseDistortionPacket[] pdccxf2 = { pdcxf2 };
		//wave3 = new Inverter(pdccxf2);
		
		//wave3 = new SpreadWaveform( wave3 , (int) ( 20 ) , ( (int) ( 600 * 300 ) ) );
		
	    // wave3 = (WaveForm)( r.get( 6 ) );
		
		// ...
		
		return( wave3 );
		
	}
	
	
	
	
	
	
	
	
	
	

}