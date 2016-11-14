package jpl.ch03.ex06;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Test;

import jpl.ch03.ex06.EnergySource.WrongEnegryUsageException;
import jpl.ch03.ex06.Vehicle.WrongVehicleUsageException;

public class PassengerVehicleTest {
	@After
	public void doAfterEachTest() {
		Vehicle.setNextId(1);
	}
	
	/**
	 * 引数１つのconstructor.
	 * 
	 * 正常系
	 * @throws WrongEnegryUsageException 
	 * @throws WrongVehicleUsageException 
	 */
	@Test
	public void testConstructorWithOneParam() throws WrongEnegryUsageException, WrongVehicleUsageException {
		int seatCapacity = 0;
		EnergySource[] energySources = new EnergySource[2];
		energySources[0] = new GasTank(20, 20);
		energySources[1] = new Battery(10, 10);
				
		PassengerVehicle pvA = new PassengerVehicle(energySources, seatCapacity);
		assertTrue(pvA instanceof PassengerVehicle);
		assertTrue(pvA instanceof Vehicle);
		assertEquals(seatCapacity, pvA.getSeatingCapacity());
	}
	
	/**
	 * 引数２つのconstructor.
	 * 
	 * 正常系
	 */
	@Test
	public void testConstructorWithTwoParams() throws WrongEnegryUsageException, WrongVehicleUsageException {
		int seatCapacity = 0;
		String owner = "A";
		EnergySource[] energySources = new EnergySource[2];
		energySources[0] = new GasTank(20, 20);
		energySources[1] = new Battery(10, 10);
		
		PassengerVehicle pvA = new PassengerVehicle(energySources, seatCapacity, owner);
		assertTrue(pvA instanceof PassengerVehicle);
		assertTrue(pvA instanceof Vehicle);
		assertEquals(seatCapacity, pvA.getSeatingCapacity());
		assertThat(pvA.getOwner(), is(owner));
	}
	
	/**
	 * アクセッサーメソッド
	 * 
	 * 正常系
	 */
	@Test
	public void testAccessorMethods() throws WrongEnegryUsageException, WrongVehicleUsageException {
		EnergySource[] energySources = new EnergySource[2];
		energySources[0] = new GasTank(20, 20);
		energySources[1] = new Battery(10, 10);
		int seatCapacity = 2;
		String owner = "A";
		double speed = 60.2;
		double direction = 18.4;
		int occupantNum = 1;
		
		PassengerVehicle pvA = new PassengerVehicle(energySources, seatCapacity);
		pvA.setOwner(owner);
		pvA.setSpeed(speed);
		pvA.setDirection(direction);
		pvA.setOccupantNum(occupantNum);
		
		assertEquals(1, pvA.getId());
		assertEquals(2, PassengerVehicle.getNextId());
		assertEquals(seatCapacity, pvA.getSeatingCapacity());
		assertEquals(owner, pvA.getOwner());
		assertEquals((Double)speed, (Double)pvA.getSpeed());
		assertEquals((Double)direction, (Double)pvA.getDirection());
		assertEquals(occupantNum, pvA.getOccupantNum());
	}

	/**
	 * インスタンスの各フィールドを表した１つの文字列を返す.
	 * VehicleクラスのtoStringメソッドをオーバーライドしている。
	 * 
	 * 正常系：すべてのフィールドが初期化されている場合
	 */
	@Test
	public void testToStringWithAllFieldsSet() throws WrongEnegryUsageException, WrongVehicleUsageException {
		EnergySource[] energySources = new EnergySource[2];
		energySources[0] = new GasTank(20, 20);
		energySources[1] = new Battery(10, 10);
		int seatCapacity = 2;
		String owner = "A";
		double speed = 60.2;
		double direction = 18.4;
		int occupantNum = 1;
		
		PassengerVehicle pvA = new PassengerVehicle(energySources, seatCapacity, owner);
		pvA.setSpeed(speed);
		pvA.setDirection(direction);
		pvA.setOccupantNum(occupantNum);
		assertThat(pvA.toString(), is("Vehicle No.1: owner=" + owner + ", seats=" 
									+ seatCapacity + ", occupants=" + occupantNum + ", speed=" 
									+ speed + "km/h, direction=" + direction));

	}

	/**
	 * インスタンスの各フィールドを表した１つの文字列を返す.
	 * VehicleクラスのtoStringメソッドをオーバーライドしている。
	 * 
	 * 正常系：すべてのフィールドが初期化されていない場合
	 */
	@Test
	public void testToStringWithSomeFieldsSet() throws WrongEnegryUsageException, WrongVehicleUsageException {
		EnergySource[] energySources = new EnergySource[2];
		energySources[0] = new GasTank(20, 20);
		energySources[1] = new Battery(10, 10);
		int seatCapacity = 2;
		String owner = null;
		double speed = 0.0;
		double direction = 0.0;
		int occupantNum = 0;
		
		PassengerVehicle pvA = new PassengerVehicle(energySources, seatCapacity);
		assertThat(pvA.toString(), is("Vehicle No.1: owner=" + owner + ", seats=" 
									+ seatCapacity + ", occupants=" + occupantNum + ", speed=" 
									+ speed + "km/h, direction=" + direction));

	}
	/**
	 * PassengerVehicleクラスのインスタンス３つを生成し、
	 * それらをコンソール上に出力する. 
	 * 
	 * 正常系
	 */
	@Test
	public void testMain() throws WrongEnegryUsageException, WrongVehicleUsageException {
		ByteArrayOutputStream out = new  ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		
		PassengerVehicle.main(new String[0]);
		
		assertThat(out.toString(), is("Vehicle No.1: owner=A, seats=2, occupants=1, speed=60.2km/h, direction=18.4" + System.lineSeparator()
										+ "Vehicle No.2: owner=B, seats=4, occupants=3, speed=63.2km/h, direction=-98.4" + System.lineSeparator()
										+ "Vehicle No.3: owner=C, seats=10, occupants=5, speed=43.5km/h, direction=-2.4" + System.lineSeparator()));
	}

}