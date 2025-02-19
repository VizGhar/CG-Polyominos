import java.util.ArrayList;
import java.util.Scanner;

public class TestPlayer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int turn = 1;
        while (true) {
            String remainingTiles = scanner.nextLine();
            String currentTile = scanner.nextLine();

            System.err.println(remainingTiles);
            System.err.println(currentTile);

            int h = scanner.nextInt();
            int w = scanner.nextInt();
            System.err.println(h + " " + w);
            scanner.nextLine();
            ArrayList<String> map = new ArrayList<>();
            for (int i = 0; i < h; i++) {
                map.add(scanner.nextLine());
                System.err.println(map.get(map.size() - 1));
            }
            if (turn == 1) {
                System.out.println(".O.;.O.;OOO");
                System.out.println("8 2");
            } else if (turn == 2) {
                System.out.println("..O;OOO;O..");
                System.out.println("1 4");
            } else if (turn == 3) {
                System.out.println("OOO;..O;..O");
                System.out.println("0 1");
            } else if (turn == 4) {
                System.out.println("O.;OO;.O");
                System.out.println("3 1");
            } else if (turn == 5){
                System.out.println("O..;OOO");
                System.out.println("0 0");
            } else if (turn == 6) {
                System.out.println("OOO;OO.");
                System.out.println("0 4");
            } else if (turn == 7){
                System.out.println(".OO;OO.;O..");
                System.out.println("3 4");
            } else if (turn == 8){
                System.out.println("OOO.;O.OO");
                System.out.println("2 0");
            } else {
                System.out.println("O;O;O;O");
                System.out.println("4 3");
            }
            turn++;
        }
    }
}
