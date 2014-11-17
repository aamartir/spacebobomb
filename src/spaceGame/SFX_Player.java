package spaceGame;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SFX_Player 
{
	// Paths
	public static final String MALE_SOUND_PATH 	 = "/resources/SFX/Words/Male/";
	public static final String FEMALE_SOUND_PATH = "/resources/SFX/Words/Female/";
	public static final String SPACE_SOUND_PATH  = "/resources/SFX/Space/";
	public static final String OTHER_SOUND_PATH  = "/resources/SFX/Other/";
	
	// Commentator's voice
	public static final String DOUBLE_KILL = "doublekill.wav";
	public static final String TRIPLE_KILL = "triplekill.wav";
	public static final String TEAM_KILLER = "teamkiller.wav";
	
	public static final String ULTRA_KILL = "ultrakill.wav";
	public static final String MEGA_KILL  = "multikill.wav";
	public static final String MULTI_KILL = "megakill.wav";
	public static final String LUDI_KILL  = "ludicrouskill.wav";
	
	public static final String UNSTOPPABLE 	 = "unstoppable.wav";
	public static final String IMPRESSIVE  	 = "impressive.wav";
	public static final String HOLY_SHIT   	 = "holyshit.wav";
	public static final String GOD_LIKE    	 = "godlike.wav";
	public static final String WICKED_SICK 	 = "wickedsick.wav";
	public static final String KILLING_SPREE = "killingspree.wav";
	
	// Button (assorted) Sound Effects
	public static final String BUTTON_3_SOUND 	= "button-3.wav";
	public static final String BUTTON_21_SOUND 	= "button-21.wav";
	public static final String BUTTON_32_SOUND 	= "button-32.wav";
	public static final String BUTTON_33_SOUND 	= "button-33.wav";
	public static final String BEEP_21_SOUND 	= "beep-21.wav";
	public static final String BEEP_22_SOUND 	= "beep-22.wav";
	public static final String BEEP_29_SOUND 	= "beep-29.wav";
	
	public static final String[] kills = {TEAM_KILLER, LUDI_KILL, IMPRESSIVE, 
										  GOD_LIKE, KILLING_SPREE, HOLY_SHIT, 
										  UNSTOPPABLE, WICKED_SICK, MEGA_KILL};

	// Space Sound Effects
	public static final String EXPLOSION_01  	  = "shipExplosion_01.wav";
	public static final String EXPLOSION_02  	  = "shipExplosion_02.wav";
	public static final String IMPLOSION_01  	  = "implosion-01.wav";
	public static final String ASTEROID_EXPLOSION = "asteroidExplosion.wav";

	
	private static Random randGen = new Random();
	
	public static synchronized void playSound(final String path, final String soundFilename) 
	{
	    new Thread(new Runnable() {
	      public void run() {
	        try {
	          Clip clip = AudioSystem.getClip();
	          AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(path + "" + soundFilename));
	          clip.open(inputStream);
	          clip.start(); 
	        } catch (Exception e) {
	          System.err.println("Error with file " + soundFilename + ". Msg: " + e.getMessage());
	        }
	      }
	    }).start();
	}
	
	public static void playRandomCommentatorSound()
	{
		playSound(MALE_SOUND_PATH, kills[randGen.nextInt(kills.length)]);
	}
}
