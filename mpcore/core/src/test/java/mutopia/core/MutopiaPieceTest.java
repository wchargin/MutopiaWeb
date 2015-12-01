package mutopia.core;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class MutopiaPieceTest {
	private static final String LILYPOND_VERSION = "2.19.31";

	@Test
	public void testMutopiaPiece() {
		MutopiaPiece mp = new MutopiaPiece("foo.ly");
		Map<String, PropertyDescriptor> piecePropertyMap = mp.getPiecePropertyMap();
		
		// Pick a random property to test
		assertTrue(piecePropertyMap.containsKey("composer"));
	}

	@Test
	public void testPopulateField() {
		MutopiaPiece mp = new MutopiaPiece("foo.ly");
		mp.populateField("mutopiacomposer", "BachJS");
		assertEquals("BachJS", mp.getComposer());
		mp.populateField("composer", "ChopinFF");
		
		// A "composer" should not overwrite a "mutopiacomposer"
		assertEquals("BachJS", mp.getComposer());
	}

	@Test
	public void testDeriveCompileStuff() {
		MutopiaPiece mp = doDeriveCompilerStuff();
		
		assertEquals("djinns", mp.getFilenameBase());
		assertFalse(mp.getMultipleLyFiles());
		assertTrue(mp.getMultipleMidFiles());
		assertFalse(mp.getMultiplePdfFiles());
		assertEquals(LILYPOND_VERSION, mp.getLilyVersion());
		assertEquals(new Integer(281), mp.getPreviewHeight()); 
		assertEquals(new Integer(483), mp.getPreviewWidth()); 
	}

	@Test
	public void testGetLilyVersionString() {
		String lilyPondExe = System.getenv("LILYPOND_BIN");
		
		if (lilyPondExe == null) {
			fail("Can't find LILYPOND_BIN");
		}
		
		String version = "not found";
		try {
			version = MutopiaPiece.getLilyVersion(lilyPondExe);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertEquals(LILYPOND_VERSION, version);
	}

	@Test
	public void testCheckFieldConsistency() {
		MutopiaPiece mp = new MutopiaPiece("foo.ly");
		mp.populateField("mutopiacomposer", "ChopinFF");
		mp.populateField("title", "The Foo Bird Sings");
		mp.populateField("instrument", "Piano");
		mp.populateField("style", "Romantic");
		mp.populateField("source", "A little bird told me");
		
		// Haven't set license yet, so fail
		assertFalse(mp.checkFieldConsistency(false));
		
		mp.populateField("license", "Public Domain");
		assertTrue(mp.checkFieldConsistency(false));
	}

	@Test
	public void testCheckCompileConsistency() {
		MutopiaPiece mp = doDeriveCompilerStuff();
		assertTrue(mp.checkCompileConsistency());
	}
	
	private MutopiaPiece doDeriveCompilerStuff() {
		char fs = File.separatorChar;
		MutopiaPiece mp = new MutopiaPiece("src" + fs + "test" + fs + "resources" + fs + "djinns.ly");
		String lilyPondExe = System.getenv("LILYPOND_BIN");
		
		if (lilyPondExe == null) {
			fail("Can't find LILYPOND_BIN");
		}
		
		try {
			mp.deriveCompileStuff(lilyPondExe);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		return mp;
	}

}
