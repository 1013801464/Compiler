import com.sun.istack.internal.NotNull;

import java.util.Scanner;

public class Main {

    /**
     * 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^
     **/
    static int calculate(String[] locations) {
        int[][] locs = new int[locations.length + 1][2];        // 存储解析的坐标
        int[][] dist = new int[locations.length + 1][locations.length + 1]; // 存储各点距离
        locs[0][0] = 0;
        locs[0][1] = 0;
        for (int i = 1; i <= locations.length; ++i) {
            int co = locations[i - 1].indexOf(",");
            locs[i][0] = Integer.parseInt(locations[i - 1].substring(0, co));
            locs[i][1] = Integer.parseInt(locations[i - 1].substring(co + 1, locations[i - 1].length()));
        }
        for (int i = 0; i <= locations.length; ++i) {
            for (int j = 0; j <= locations.length; ++j) {
                dist[i][j] = Math.abs(locs[i][0] - locs[j][0]) + Math.abs(locs[i][1] - locs[j][1]);
            }
        }
        boolean[] used = new boolean[locations.length + 1];
        for (int i = 0; i < locations.length + 1; ++i) {
            used[i] = false;
        }
        return minDist(0, 0, used, dist);
    }

    /*返回从start开始的最短距离 */
    static int minDist(int start, int state, @NotNull boolean[] used, int[][] dist) {
        int len = used.length;       // 点的个数
        if (state == len - 1) {
            return dist[start][0];
        } else {
            used[start] = true;
            int maxValue = Integer.MAX_VALUE;
            for (int i = 0; i < len; i++) {
                if (used[i]) continue;
                int d = minDist(i, state + 1, used, dist);
                if (d + dist[start][i] < maxValue) {
                    maxValue = d + dist[start][i];
                }
            }
            used[start] = false;
            return maxValue;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine().trim());
        int index = 0;
        String[] locations = new String[num];
        while (num-- > 0) {
            locations[index++] = scanner.nextLine();
        }

        System.out.println(calculate(locations));
    }
}