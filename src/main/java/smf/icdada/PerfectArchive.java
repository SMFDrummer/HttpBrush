package smf.icdada;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import smf.icdada.HttpUtils.Check;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static smf.icdada.HttpUtils.Base.*;
import static smf.icdada.RequestType.V302;
import static smf.icdada.RequestType.V316;

public class PerfectArchive {
    public static void single() {
        Log.v("请输入拓维userId");
        String userId = smfScanner.String(true, "^.{8,}$");
        Log.v("""
                请输入需要执行的功能
                [1] 全神器刷取
                [2] 全植物刷取
                [3] 全装扮刷取""");
        switch (smfScanner.Int(false, "^[1|2|3]$")) {
            case 1 -> rewardArtifactBrush(userId);
            case 2 -> rewardPlantBrush(userId);
            case 3 -> rewardCustomBrush(userId);
        }
    }

    public static void measure() {
        initUsersMap();
        List<String> users = getUserList();
        Log.v("""
                请输入需要执行的功能
                [1] 全神器刷取
                [2] 全植物刷取
                [3] 全装扮刷取""");
        int index = smfScanner.Int(false, "^[1|2|3]$");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (String userId : users) {
            sleep(100);
            executor.submit(() -> {
                switch (index) {
                    case 1 -> rewardArtifactBrush(userId);
                    case 2 -> rewardPlantBrush(userId);
                    case 3 -> rewardCustomBrush(userId);
                }
                UserJsonUtils.JsonUtil(userId, "activate", false);
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.s("所有账号处理完成，程序退出");
        System.exit(0);
    }

    private static void rewardArtifactBrush(String userId) {
        Check.V316 v316 = new Check.V316();
        Check.V302 v302 = new Check.V302();
        ProgressBar progressBar = new ProgressBar("账号: " + userId + " 刷取进度 || ", 3, true);
        refresh(userId);
        while (true) {
            Future<String> futureV316 = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(futureV316.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0)) {
                    int uk = Integer.parseInt(v316.data.get("$.p.uk").toString());
                    progressBar.print(1);
                    while (true) {
                        int artifactUk = ++uk;
                        Future<String> futureV302Artifact = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardPieces(artifactUk), artifactUk));
                        try {
                            v302.setResponseBody(futureV302Artifact.get(10, TimeUnit.SECONDS));
                            if (v302.isValid(11102)) {
                                progressBar.print(1);
                                uk = uk + 1;
                            } else if (v302.isValid(0)) {
                                progressBar.print(2);
                                int i = 0;
                                while (i != 1) {
                                    uk++;
                                    int plantUk = uk;
                                    Future<String> futureV302Plant = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardArtifacts(plantUk), plantUk));
                                    try {
                                        v302.setResponseBody(futureV302Plant.get(10, TimeUnit.SECONDS));
                                        if (v302.isValid(0)) i++;
                                        progressBar.print(2 + i);
                                    } catch (Exception ignored) {
                                        refresh(userId);
                                    }
                                }
                                return;
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    private static void rewardPlantBrush(String userId) {
        Check.V316 v316 = new Check.V316();
        Check.V302 v302 = new Check.V302();
        ProgressBar progressBar = new ProgressBar("账号: " + userId + " 刷取进度 || ", 25, true);
        refresh(userId);
        while (true) {
            Future<String> futureV316 = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(futureV316.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0)) {
                    int uk = Integer.parseInt(v316.data.get("$.p.uk").toString());
                    progressBar.print(1);
                    while (true) {
                        int artifactUk = ++uk;
                        Future<String> futureV302Artifact = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardPieces(artifactUk), artifactUk));
                        try {
                            v302.setResponseBody(futureV302Artifact.get(10, TimeUnit.SECONDS));
                            if (v302.isValid(11102)) {
                                progressBar.print(1);
                                uk = uk + 1;
                            } else if (v302.isValid(0)) {
                                progressBar.print(2);
                                int i = 0;
                                while (i != 23) {
                                    uk++;
                                    int plantUk = uk;
                                    Future<String> futureV302Plant = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardPlants(plantUk), plantUk));
                                    try {
                                        v302.setResponseBody(futureV302Plant.get(10, TimeUnit.SECONDS));
                                        if (v302.isValid(0)) i++;
                                        progressBar.print(2 + i);
                                    } catch (Exception ignored) {
                                        refresh(userId);
                                    }
                                }
                                return;
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    private static void rewardCustomBrush(String userId) {
        Check.V316 v316 = new Check.V316();
        Check.V302 v302 = new Check.V302();
        ProgressBar progressBar = new ProgressBar("账号: " + userId + " 刷取进度 || ", 3, true);
        refresh(userId);
        while (true) {
            Future<String> futureV316 = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(futureV316.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0)) {
                    int uk = Integer.parseInt(v316.data.get("$.p.uk").toString());
                    progressBar.print(1);
                    while (true) {
                        int artifactUk = ++uk;
                        Future<String> futureV302Artifact = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardPieces(artifactUk), artifactUk));
                        try {
                            v302.setResponseBody(futureV302Artifact.get(10, TimeUnit.SECONDS));
                            if (v302.isValid(11102)) {
                                progressBar.print(1);
                                uk = uk + 1;
                            } else if (v302.isValid(0)) {
                                progressBar.print(2);
                                int i = 0;
                                while (i != 1) {
                                    uk++;
                                    int customUk = uk;
                                    Future<String> futureV302Custom = getExecutor(userId).submit(() -> getResponseBody(V302, userId, rewardCustoms(customUk), customUk));
                                    try {
                                        sleep(10000);
                                        v302.setResponseBody(futureV302Custom.get(10, TimeUnit.SECONDS));
                                        if (v302.isValid(0)) i++;
                                        progressBar.print(2 + i);
                                    } catch (Exception ignored) {
                                        refresh(userId);
                                    }
                                }
                                return;
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    public static void invalidChecker() {
        Log.v("请输入拓维userId");
        String userId = smfScanner.String(true, "^.{8,}$");
        refresh(userId);
        Check.V316 v316 = new Check.V316();
        int[][] ranges = {
                {}
        };
        while (true) {
            Future<String> futureV316 = getExecutor(userId).submit(() -> getResponseBody(V316, userId));
            try {
                v316.setResponseBody(futureV316.get(3, TimeUnit.SECONDS));
                if (v316.isValid(0)) {
                    int uk = Integer.parseInt(v316.data.get("$.p.uk").toString());
                    Check.V302 v302 = new Check.V302();
                    for (int[] range : ranges) {
                        JSONArray array = new JSONArray();
                        int finalUk = ++uk;
                        if (range.length == 1) {
                            array.add(rewardObjectFormat(range[0], finalUk));
                        } else {
                            IntStream.range(range[0], range[1] + 1).forEach(i -> array.add(rewardObjectFormat(i, finalUk)));
                        }
                        Future<String> futureV302 = getExecutor(userId).submit(() -> getResponseBody(V302, userId, array, finalUk));
                        try {
                            v302.setResponseBody(futureV302.get(3, TimeUnit.SECONDS));
                            if (v302.isValid(20712)) {
                                Log.e(Arrays.toString(range) + "is Invalid!");
                            }
                        } catch (Exception ignored) {
                            refresh(userId);
                        }
                    }
                    return;
                }
            } catch (Exception ignored) {
                refresh(userId);
            }
        }
    }

    private static JSONArray rewardPieces(int uk) {
        JSONArray array = new JSONArray();
        IntStream.range(22050, 22074).forEach(i -> array.add(rewardPieceFormat(i, uk)));
        return array;
    }

    private static JSONArray rewardArtifacts(int uk) {
        JSONArray array = new JSONArray();
        int[][] ranges = {
                {60001, 60101}
        };
        return addRewardObjects(array, ranges, uk);
    }

    private static JSONArray rewardPlants(int uk) {
        JSONArray array = new JSONArray();
        int[][] ranges = {
                {1001, 1037},
                {1039, 1045},
                {1047},
                {1049, 1086},
                {1088, 1095},
                {1097, 1099},
                {111001, 111004},
                {111006},
                {111008, 111056},
                {111058},
                {111060, 111076},
                {111078, 111079},
                {111081, 111082},
                {111084, 111091},
                {200000, 200035},
                {200037, 200039},
                {200041},
                {200043, 200074}
        };
        return addRewardObjects(array, ranges, uk);
    }

    private static JSONArray rewardCustoms(int uk) {
        JSONArray array = new JSONArray();
        int[][] ranges = {
                {1201, 1237},
                {1239, 1245},
                {1247},
                {1249, 1286},
                {1288, 1295},
                {1297, 1299},

                {111201, 111204},
                {111206},
                {111208, 111256},
                {111258},
                {111260, 111276},
                {111278, 111279},
                {111281, 111282},
                {111284, 111291},

                {30010011, 30010012},
                {30010021, 30010025},
                {30010031, 30010032},
                {30010041, 30010042},
                {30010053, 30010054},
                {30010061, 30010062},
                {30010071, 30010072},
                {30010081},
                {30010091, 30010093},
                {30010101},
                {30010121, 30010123},
                {30010141},
                {30010151},
                {30010161},
                {30010191, 30010192},
                {30010201},
                {30010211},
                {30010213},
                {30010221, 30010222},
                {30010231, 30010232},
                {30010241, 30010242},
                {30010244},
                {30010271},
                {30010273},
                {30010281, 30010282},
                {30010291, 30010292},
                {30010301, 30010302},
                {30010311},
                {30010313},
                {30010372, 30010373},
                {30010391, 30010392},
                {30010401},
                {30010421},
                {30010431},
                {30010441},
                {30010491},
                {30010691},
                {30010701},
                {30010811, 30010812},
                {30010821},
                {30010831},
                {30010861},
                {30010931},
                {30010991},

                {31110081},
                {31110091},
                {31110101},
                {31110121},
                {31110151},
                {31110161, 31110162},
                {31110191},
                {31110221},
                {31110231},
                {31110251},
                {31110261},
                {31110271},
                {31110291},
                {31110301},
                {31110321},
                {31110331},
                {31110351, 31110352},
                {31110361},
                {31110381},
                {31110391},
                {31110401},
                {31110431},
                {31110441},
                {31110451, 31110452},
                {31110461},
                {31110471},
                {31110481},
                {31110521},
                {31110531},
                {31110551},
                {31110561},
                {31110611},
                {31110621, 31110622},
                {31110631},
                {31110671},
                {31110691},
                {31110701, 31110702},
                {31110751, 31110752},
                {31110791},
                {31110841},
                {31110851},
                {31110861},
                {31110871},
                {31110881},
                {31110891},
                {31110901, 31110902},

                {32000000},
                {32000010, 32000011},
                {32000020},
                {32000030},
                {32000040},
                {32000050},
                {32000060},
                {32000070, 32000072},
                {32000080},
                {32000090, 32000092},
                {32000100, 32000101},
                {32000110, 32000111},
                {32000120},
                {32000130},
                {32000140},
                {32000150},
                {32000160},
                {32000170, 32000171},
                {32000180},
                {32000190, 32000192},
                {32000200},
                {32000210, 32000211},
                {32000220},
                {32000230},
                {32000240},
                {32000250},
                {32000260},
                {32000270, 32000271},
                {32000280},
                {32000290},
                {32000300},
                {32000310},
                {32000320, 32000321},
                {32000330},
                {32000340, 32000341},
                {32000350},
                {32000370},
                {32000380, 32000381},
                {32000390, 32000391},
                {32000410},
                {32000430},
                {32000440},
                {32000450},
                {32000460},
                {32000470},
                {32000480},
                {32000490},
                {32000500},
                {32000510, 32000512},
                {32000520},
                {32000530, 32000531},
                {32000540},
                {32000550},
                {32000560, 32000561},
                {32000570},
                {32000580, 32000581},
                {32000590},
                {32000600},
                {32000610},
                {32000620},
                {32000630},
                {32000640},
                {32000650},
                {32000660},
                {32000670, 32000671},
                {32000680, 32000681},
                {32000690},
                {32000700},
                {32000710},
                {32000720},
                {32000730},
                {32000740}
        };
        return addRewardObjects(array, ranges, uk);
    }

    private static JSONObject rewardPieceFormat(int rewardId, int uk) {
        JSONObject object = new JSONObject();
        object.put("i", rewardId);
        object.put("q", 60);
        object.put("f", "steam" + uk + "_hard_level_reward");
        return object;
    }

    private static JSONObject rewardObjectFormat(int rewardId, int uk) {
        JSONObject object = new JSONObject();
        object.put("i", rewardId);
        object.put("q", 1);
        object.put("f", "steam" + uk + "_hard_level_reward");
        return object;
    }

    private static JSONArray addRewardObjects(JSONArray array, int[][] ranges, int uk) {
        for (int[] range : ranges) {
            if (range.length == 1) {
                array.add(rewardObjectFormat(range[0], uk));
            } else {
                IntStream.range(range[0], range[1] + 1).forEach(i -> array.add(rewardObjectFormat(i, uk)));
            }
        }
        return array;
    }
}