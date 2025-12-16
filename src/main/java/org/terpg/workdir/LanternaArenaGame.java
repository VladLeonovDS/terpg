package org.terpg.workdir;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class LanternaArenaGame {
    private final GameConfig config;
    private final SplittableRandom rng;
    private final Player player = new Player(120, 20, 15);
    private final List<Enemy> enemyQueue = new ArrayList<>();
    private final List<String> battleLog = new ArrayList<>();
    private Screen screen;

    public LanternaArenaGame(GameConfig config) {
        this.config = config;
        this.rng = new SplittableRandom(config.seed());
    }

    public void run() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        screen = new TerminalScreen(factory.createTerminalEmulator());
        screen.startScreen();
        seedUtilities();
        ensureQueueFilled();
        try {
            selectionLoop();
        } finally {
            screen.stopScreen();
        }
    }

    private void selectionLoop() throws IOException {
        int index = 0;
        while (player.hp() > 0 && !enemyQueue.isEmpty()) {
            renderSelection(index);
            KeyStroke key = screen.readInput();
            if (key == null) {
                continue;
            }
            if (key.getKeyType() == KeyType.EOF) {
                break;
            }
            if (key.getKeyType() == KeyType.Character && key.getCharacter() != null) {
                char c = Character.toLowerCase(key.getCharacter());
                if (c == 'q') {
                    break;
                }
            }
            if (key.getKeyType() == KeyType.ArrowUp) {
                index = (index - 1 + enemyQueue.size()) % enemyQueue.size();
                SoundEffects.blipMenu();
            } else if (key.getKeyType() == KeyType.ArrowDown) {
                index = (index + 1) % enemyQueue.size();
                SoundEffects.blipMenu();
            } else if (key.getKeyType() == KeyType.Enter) {
                Enemy chosen = enemyQueue.remove(index);
                if (index >= enemyQueue.size()) {
                    index = Math.max(0, enemyQueue.size() - 1);
                }
                boolean survived = runBattle(chosen);
                if (!survived) {
                    break;
                }
                ensureQueueFilled();
            }
        }
    }

    private boolean runBattle(Enemy enemy) throws IOException {
        battleLog.clear();
        int turn = 1;
        while (player.hp() > 0 && !enemy.isDefeated()) {
            player.restoreEnergy(3);
            appendLog("Ход " + turn + ": ваша фаза");
            renderBattle(enemy, "Ваш ход: [Q] Атака или [W] Техника");
            String playerLine = playerTurn(enemy);
            appendLog(playerLine);
            if (enemy.isDefeated()) {
                break;
            }
            appendLog("Ход " + turn + ": атака врага");
            renderBattle(enemy, "Вражеский ход...");
            String enemyLine = enemyTurn(enemy);
            appendLog(enemyLine);
            turn += 1;
        }
        if (enemy.isDefeated()) {
            appendLog("Враг " + enemy.type() + " повержен!");
            SoundEffects.victory();
        } else {
            appendLog("Игрок пал в бою");
        }
        renderBattle(enemy, "Нажмите [Enter], чтобы вернуться к выбору");
        waitForEnter();
        return player.hp() > 0;
    }

    private String playerTurn(Enemy enemy) throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            if (key == null) {
                continue;
            }
            if (key.getKeyType() == KeyType.Character && key.getCharacter() != null) {
                char c = Character.toLowerCase(key.getCharacter());
                if (c == 'q') {
                    long baseDamage = Math.max(4, 8 - enemy.representation().base());
                    enemy.representation().damage(baseDamage);
                    SoundEffects.hit();
                    return "Вы ударили на " + baseDamage + " HP";
                }
                if (c == 'w') {
                    Utility utility = chooseUtility();
                    renderBattle(enemy, "Ваш ход продолжается");
                    if (utility == null) {
                        continue;
                    }
                    UtilityResult result = utility.apply(player, enemy, rng.split());
                    if (result.success()) {
                        SoundEffects.sparkle();
                    }
                    return result.logLine();
                }
            }
        }
    }

    private Utility chooseUtility() throws IOException {
        int idx = 0;
        while (true) {
            renderUtilityList(idx);
            KeyStroke key = screen.readInput();
            if (key == null) {
                continue;
            }
            if (key.getKeyType() == KeyType.Escape) {
                return null;
            }
            if (key.getKeyType() == KeyType.ArrowUp) {
                idx = (idx - 1 + player.utilities().size()) % player.utilities().size();
                SoundEffects.blipMenu();
            } else if (key.getKeyType() == KeyType.ArrowDown) {
                idx = (idx + 1) % player.utilities().size();
                SoundEffects.blipMenu();
            } else if (key.getKeyType() == KeyType.Enter) {
                return player.utilities().get(idx);
            }
        }
    }

    private String enemyTurn(Player player, Enemy enemy) {
        if (enemy.frozenTurns() > 0) {
            enemy.tickFrozen();
            return "Враг заморожен";
        }
        long damage = rng.nextLong(4, 9);
        boolean twist = rng.nextBoolean();
        if (twist) {
            int newBase = Math.max(2, enemy.representation().base() - 1);
            enemy.representation().setBase(newBase);
            return "Враг исказил своё основание до " + newBase;
        }
        player.damage(damage);
        SoundEffects.hit();
        return "Враг наносит " + damage + " урона";
    }

    private String enemyTurn(Enemy enemy) {
        return enemyTurn(player, enemy);
    }

    private void renderSelection(int cursor) throws IOException {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();
        g.putString(2, 1, "Undertale-like арена (Lanterna)", SGR.BOLD);
        g.putString(2, 3, "Выберите следующего врага и нажмите Enter (q — выход)");
        g.putString(size.getColumns() - 25, 1, "HP:" + player.hp());
        g.putString(size.getColumns() - 25, 2, "Shield:" + player.shield());
        g.putString(size.getColumns() - 25, 3, "Energy:" + player.energy());
        int y = 5;
        for (int i = 0; i < enemyQueue.size(); i++) {
            Enemy enemy = enemyQueue.get(i);
            String marker = i == cursor ? "→ " : "  ";
            String line = marker + enemy.type() + "  HP:" + enemy.representation().shownHp() + "  base:" + enemy.representation().base();
            if (i == cursor) {
                g.putString(4, y + i, line, SGR.BOLD);
            } else {
                g.putString(4, y + i, line);
            }
        }
        screen.refresh();
    }

    private void renderUtilityList(int cursor) throws IOException {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();
        g.putString(2, 1, "Выбор утилиты (Esc — назад)", SGR.BOLD);
        g.putString(2, 3, "Энергия: " + player.energy());
        int y = 5;
        for (int i = 0; i < player.utilities().size(); i++) {
            Utility utility = player.utilities().get(i);
            String marker = i == cursor ? "▶" : " ";
            String text = marker + " " + utility.name() + " [" + utility.tag() + "] — " + utility.description() + " (" + utility.energyCost() + ")";
            if (i == cursor) {
                g.enableModifiers(SGR.BOLD);
            }
            g.putString(2, y + i, text);
            g.disableModifiers(SGR.BOLD);
        }
        screen.refresh();
    }

    private void renderBattle(Enemy enemy, String prompt) throws IOException {
        screen.clear();
        TerminalSize size = screen.getTerminalSize();
        TextGraphics g = screen.newTextGraphics();
        g.putString(2, 1, "Бой против " + enemy.type(), SGR.BOLD);
        g.putString(size.getColumns() - 25, 1, "HP:" + player.hp());
        g.putString(size.getColumns() - 25, 2, "Shield:" + player.shield());
        g.putString(size.getColumns() - 25, 3, "Energy:" + player.energy());
        g.putString(2, 3, "HP врага: " + enemy.representation().shownHp() + " (base " + enemy.representation().base() + ")");
        g.putString(2, 5, "❤", SGR.BOLD);
        g.drawRectangle(new TerminalPosition(1, 6), new TerminalSize(size.getColumns() - 2, 6), '*');
        g.putString(3, 7, prompt);
        int logStart = Math.max(0, battleLog.size() - 4);
        int y = 8;
        g.setForegroundColor(TextColor.ANSI.CYAN);
        for (int i = logStart; i < battleLog.size(); i++) {
            g.putString(3, y++, battleLog.get(i));
        }
        screen.refresh();
    }

    private void waitForEnter() throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            if (key != null && key.getKeyType() == KeyType.Enter) {
                return;
            }
        }
    }

    private void appendLog(String line) {
        battleLog.add(line);
        if (battleLog.size() > 10) {
            battleLog.remove(0);
        }
    }

    private void ensureQueueFilled() {
        while (enemyQueue.size() < 3) {
            enemyQueue.add(generateEnemy());
        }
    }

    private Enemy generateEnemy() {
        EnemyType type = EnemyType.values()[rng.nextInt(EnemyType.values().length)];
        long hp = rng.nextLong(40, 120);
        int base = rng.nextInt(2, 16);
        HPRepresentation representation = new HPRepresentation(hp, base, rng.nextBoolean(), rng.nextBoolean(), rng.nextInt(0, 4) == 0);
        return new Enemy(type, hp, representation);
    }

    private void seedUtilities() {
        player.addUtility(new BaseConvUtility(10));
        player.addUtility(new FormatUtility());
        player.addUtility(new SanitizeUtility());
        player.addUtility(new HashUtility());
        player.addUtility(new GrepUtility("0"));
        player.addUtility(new SedUtility("e", "+"));
        player.addUtility(new DeterminismUtility());
        player.addUtility(new EntropyUtility());
    }
}
