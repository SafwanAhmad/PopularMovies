package com.example.android.popularmovies;

import android.provider.BaseColumns;

import com.example.android.popularmovies.data.MovieContract;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

/**
 * Local unit test, which will execute on the development machine (host).
 *@see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 * <br>Created by safwanx on 1/1/17.
 */

public class ContractClassUnitTest {

    @Test
    public void inner_class_exists() throws Exception
    {
        Class[] innerClass = MovieContract.class.getDeclaredClasses();
        assertEquals("The should be 3 inner class",3,innerClass.length);
    }

    @Test
    public void inner_class_type_correct() throws Exception
    {
        Class[] innerClasses = MovieContract.class.getDeclaredClasses();
        assertTrue("There should be some inner classes", 1 <= innerClasses.length);

        for(int i = 0; i < innerClasses.length; i++) {
            assertTrue("Inner class should be public", Modifier.isPublic(innerClasses[i].getModifiers()));
            assertTrue("Inner class should be final", Modifier.isFinal(innerClasses[i].getModifiers()));
            assertTrue("Inner class should be static", Modifier.isFinal(innerClasses[i].getModifiers()));
            assertTrue("Inner class should implements BaseColumns interface", BaseColumns.class.isAssignableFrom(innerClasses[i]));
        }
    }

    @Test
    public void inner_class_members_correct() throws Exception {
        Class[] innerClasses = MovieContract.class.getDeclaredClasses();
        assertTrue("There should be some inner classes", 1 <= innerClasses.length);

        for(int i = 0; i < innerClasses.length; i++) {
            Field[] fields = innerClasses[i].getDeclaredFields();
            assertEquals("There should be 7 fields inside each inner class", 7, fields.length);
            for (Field field : fields) {
                assertTrue("Each field should be public", Modifier.isPublic(field.getModifiers()));
                assertTrue("Each field should be static", Modifier.isStatic(field.getModifiers()));
                assertTrue("Each field should be final", Modifier.isFinal(field.getModifiers()));
                assertTrue("Each field should be of type string", field.getType() == String.class);
            }
        }
    }

}
