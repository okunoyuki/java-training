package jpl.ch03.ex04;

/**
 * Vehicleクラスを拡張したPassengerVehicleクラス.
 * Vehicleクラスのフィールド（ID, 所有者, スピード, 方向）に加えて、
 * 座席数と現在座っている人数のフィールドを持つ。
 * @author okuno
 *
 */
public class PassengerVehicle extends Vehicle {
	
	// ++++++ fields ++++++
	private final int SEATING_CAPACITY;
	private int occupantNum;
	
	// ++++++ constructors ++++++
	public PassengerVehicle(int seatingCapacity) {
		super();
		this.SEATING_CAPACITY = seatingCapacity;
	}
	
	public PassengerVehicle(int seatingCapacity, String owner) {
		super(owner);
		this.SEATING_CAPACITY = seatingCapacity;
	}
	
	/*
	 *  各privateフィールドに対する単純なsetter, 
	 *  getterは、メソッド名と仕様が完全に一致することから変更の余地がないことから、
	 *  finalを設定しても有用性の制限にはならないと考えた。
	 *  よってfinalメソッドにした。
	 */
	// ++++++ accessor methods ++++++
	public final int getSeatingCapacity() {
		return SEATING_CAPACITY;
	}
	
	public final int getOccupantNum() {
		return occupantNum;
	}
	
	public final void setOccupantNum(int occupantNum) {
		this.occupantNum = occupantNum;
	}

	// ++++++ other methods ++++++
	/*
	 *  toStringメソッドはサブクラスでフィールドが追加された場合に
	 *  オーバーライドされる可能性が高いため
	 *  finalメソッドにはしない
	 */
	/**
	 * インスタンスの各フィールドを表した１つの文字列を返す.
	 * VehicleクラスのtoStringメソッドをオーバーライドしている。
	 */
	public String toString() {
		String desc = "Vehicle No." + this.getId() + ": ";
		desc += "owner=" + this.getOwner() + ", ";
		desc += "seats=" + this.getSeatingCapacity() + ", ";
		desc += "occupants=" + this.getOccupantNum() + ", ";
		desc += "speed=" + this.getSpeed() + "km/h, "; 
		desc += "direction=" + this.getDirection();
		return desc;
	}
	
	// ++++++ main ++++++
	/*
	 *  mainメソッドはクラスごとに定義することが多いので
	 *  finalメソッドにはしない
	 */
	/**
	 * PassengerVehicleクラスのインスタンス３つを生成し、
	 * それらをコンソール上に出力する. 
	 */
	public static void main(String[] args) {
		PassengerVehicle[] vehicles = new PassengerVehicle[3];
		
		PassengerVehicle pvA = new PassengerVehicle(2, "A");
		pvA.setSpeed(60.2);
		pvA.setDirection(18.4);
		pvA.setOccupantNum(1);
		vehicles[0] = pvA;
		
		PassengerVehicle pvB = new PassengerVehicle(4, "B");
		pvB.setSpeed(63.2);
		pvB.setDirection(-98.4);
		pvB.setOccupantNum(3);
		vehicles[1] = pvB;
		
		PassengerVehicle pvC = new PassengerVehicle(10, "C");
		pvC.setSpeed(43.5);
		pvC.setDirection(-2.4);
		pvC.setOccupantNum(5);
		vehicles[2] = pvC;
		
		for (int i = 0; i < vehicles.length; i++) {
			System.out.println(vehicles[i]);	
		}
	}

}
