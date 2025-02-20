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
                System.out.println("(2,4) (3,4) (4,3) (4,4)");
            } else if (turn == 2) {
                System.out.println("(2,2) (3,1) (3,2) (3,3) (4,2)");
            } else if (turn == 3) {
                System.out.println("(0,7) (0,8) (1,6) (1,7) (1,8)");
            } else if (turn == 4) {
                System.out.println("(4,5) (4,6) (5,4) (5,5) (6,4)");
            } else if (turn == 5){
                System.out.println("(2,8) (3,7) (3,8) (4,7) (5,7) (5,8)");
            } else if (turn == 6) {
                System.out.println("(6,0) (6,1) (7,0) (8,0) (8,1)");
            } else if (turn == 7){
                System.out.println("(4,1) (5,0) (5,1)");
            } else if (turn == 8){
                System.out.println("(7,7) (7,8) (8,7) (8,8)");
            } else if (turn == 9){
                System.out.println("(5,6) (6,6) (6,7) (6,8) (7,6)");
            } else if (turn == 10){
                System.out.println("(0,0) (1,0) (2,0) (3,0)");
            } else if (turn == 11){
                System.out.println("(2,6) (2,7) (3,5) (3,6)");
            } else if (turn == 12){
                System.out.println("(0,1) (1,1) (1,2) (2,1)");
            } else if (turn == 13){
                System.out.println("(5,2) (5,3) (6,2) (7,1) (7,2)");
            }
            turn++;
        }
    }
}
