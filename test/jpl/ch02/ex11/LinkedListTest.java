package jpl.ch02.ex11;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class LinkedListTest {

	@Test
	public void testConstructorWithSingletParam() {
		Object object1 = "object1";
		Object object2 = "object2";
		
		LinkedList linkedList1 = new LinkedList(object1);
		LinkedList linkedList2 = new LinkedList(object2);
		
		linkedList1.nextItem = linkedList2;
		
		assertEquals(object1, linkedList1.object);
		assertEquals(linkedList2, linkedList1.nextItem);
	}


	@Test
	public void testConstructorWithTwoParams() {
		Object object1 = "object1";
		Object object2 = "object2";
		
		LinkedList linkedList2 = new LinkedList(object2);
		LinkedList linkedList1 = new LinkedList(object1, linkedList2);
		
		assertEquals(object1, linkedList1.object);
		assertEquals(linkedList2, linkedList1.nextItem);
	}


	@Test
	public void testMain() {
		ByteArrayOutputStream out = new  ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		LinkedList.main(new String[0]);
		
		assertThat(out.toString(), is("Vehicle No.1: speed=0.0km/h, direction=0.0, owner=null; " 
				+ "Vehicle No.2: speed=0.0km/h, direction=0.0, owner=null; " 
				+ "Vehicle No.3: speed=0.0km/h, direction=0.0, owner=null" + System.lineSeparator()));
	}

}
