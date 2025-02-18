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
            if (turn++ == 1) {
                System.out.println("OO;.O;.O");
                System.out.println("0 1");
            } else {
                System.out.println("O.;OO;O.");
                System.out.println("0 0");
            }
        }
    }
}
