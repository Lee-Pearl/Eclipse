
public class test {

	public static void main(String[] args) {

//		int x = 10;
//		int y = 20;
//		
//		int z = (y--);
//		
//		System.out.println(z);
//
//		z = y;
//		
//		System.out.println(z);

//		int x = 10;
//		int y = 5;
//		
//		System.out.print(y%2);

		
		int sum = 0;
		for(int i=1; i<=100; i++) {
			if(i%3 == 0) {
				sum += i;
				System.out.println(i);
			}
		}
		System.out.println("3배수합: " + sum);

	}

}
