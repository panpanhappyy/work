package top.jimxu.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakePanel extends JPanel implements KeyListener, ActionListener {
	// ��������ͼƬ
	ImageIcon up = new ImageIcon("up.png");
	ImageIcon down = new ImageIcon("down.png");
	ImageIcon left = new ImageIcon("left.png");
	ImageIcon right = new ImageIcon("right.png");
	ImageIcon title = new ImageIcon("title.jpg");
	ImageIcon body = new ImageIcon("body.png");
	ImageIcon food = new ImageIcon("food.png");

	// �ߵ����ݽṹ���
	int[] snakex = new int[750];
	int[] snakey = new int[750];
	int len = 3;
	String direction = "R";// R��L��U��D��

	// ʳ������
	Random r = new Random();
	int foodx = r.nextInt(34) * 25 + 25; // 34�����ӣ�һ������25�����أ�����25���ؿհ�
	int foody = r.nextInt(24) * 25 + 75; // 24�����ӣ�һ������25�����أ�����75���ؿհ�

	// ��Ϸ�Ƿ�ʼ
	boolean isStarted = false;

	// ��Ϸ�Ƿ�ʧ��
	boolean isFaild = false;

	// 关卡信息
	int level = 1; // 当前关卡
	int targetFood = 5; // 当前关卡需要吃的食物数量
	int eatenFood = 0; // 已经吃的食物数量
	int countdown = 30; // 关卡倒计时（秒）
	int score = 0; // 得分

	// 弹窗状态
	boolean showLevelUpPopup = false; // 是否显示通关弹窗
	boolean showGameOverPopup = false; // 是否显示失败弹窗

	// 存档文件路径
	private static final String SAVE_FILE_PATH = "snake_save.txt";

	// ��ʼ����
	public void initSnake() {
		isStarted = false;
		isFaild = false;
		showLevelUpPopup = false;
		showGameOverPopup = false;
		len = 3;
		direction = "R";
		snakex[0] = 100;
		snakey[0] = 100;
		snakex[1] = 75;
		snakey[1] = 100;
		snakex[2] = 50;
		snakey[2] = 100;
		// 重置关卡信息
		level = 1;
		targetFood = 5;
		eatenFood = 0;
		countdown = 30;
		score = 0;
		// 重置计时器速度
		timer.setDelay(150);
	}

	public SnakePanel() {
		this.setFocusable(true);
		// 检查是否有存档文件
		if (hasSaveFile()) {
			// 询问是否恢复上次存档
			int option = JOptionPane.showConfirmDialog(this, "是否恢复上次存档?", "恢复存档", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				loadGame();
			} else {
				initSnake(); // ���þ�̬�ߣ�
			}
		} else {
			initSnake(); // ���þ�̬�ߣ�
		}
		this.addKeyListener(this);// ���ӿ�
		timer.start();
	}

	// �������ƶ��ٶ�
	Timer timer = new Timer(150, this);

	public void paint(Graphics g) {
		// ���ñ�����ɫ
		this.setBackground(Color.black);
		g.fillRect(25, 75, 850, 600);
		// ���ñ���
		title.paintIcon(this, g, 25, 11);

		// 绘制关卡信息面板
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.drawString("Level: " + level, 700, 30);
		g.drawString("Target: " + eatenFood + "/" + targetFood, 700, 50);
		g.drawString("Time: " + countdown + "s", 700, 70);
		g.drawString("Score: " + score, 700, 90);

		// ����ͷ
		if (direction.equals("R")) {
			right.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("L")) {
			left.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("U")) {
			up.paintIcon(this, g, snakex[0], snakey[0]);
		} else if (direction.equals("D")) {
			down.paintIcon(this, g, snakex[0], snakey[0]);
		}
		// ������
		for (int i = 1; i < len; i++) {
			body.paintIcon(this, g, snakex[i], snakey[i]);
		}

		// ����ʼ��ʾ��
		if (!isStarted) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.drawString("Press Space to Start or Pause", 230, 350);
		}
		// ��ʧ����ʾ��
		if (isFaild) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.drawString("Game Over,Press Space to Start", 230, 350);
		}

		// ��ʳ��
		food.paintIcon(this, g, foodx, foody);

		// 绘制通关弹窗
		if (showLevelUpPopup) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, 900, 720);
			g.setColor(Color.WHITE);
			g.fillRect(300, 250, 300, 200);
			g.setColor(Color.BLACK);
			g.setFont(new Font("arial", Font.BOLD, 24));
			g.drawString("Congratulations!", 360, 320);
			g.drawString("Level " + level + " Completed!", 340, 350);
			g.drawString("Next Level: " + (level + 1), 350, 380);
			// 绘制下一关按钮
			g.setColor(Color.GREEN);
			g.fillRect(380, 400, 140, 40);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 18));
			g.drawString("Next Level", 410, 425);
		}

		// 绘制失败弹窗
		if (showGameOverPopup) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, 900, 720);
			g.setColor(Color.WHITE);
			g.fillRect(300, 250, 300, 200);
			g.setColor(Color.BLACK);
			g.setFont(new Font("arial", Font.BOLD, 24));
			g.drawString("Game Over", 370, 320);
			g.drawString("Final Score: " + score, 350, 350);
			g.drawString("Level Reached: " + level, 345, 380);
			// 绘制重新挑战按钮
			g.setColor(Color.RED);
			g.fillRect(380, 400, 140, 40);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 18));
			g.drawString("Try Again", 415, 425);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// ��������
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// ʵ�ֿո���ͣ ����
		if (keyCode == KeyEvent.VK_SPACE) {
			if (isFaild) {
				initSnake();
				// 删除存档文件
				deleteSaveFile();
			} else {
				// 暂停时自动存档
				if (isStarted) {
					saveGame();
				}
				isStarted = !isStarted;
			}
			// repaint();
		} // ʵ��ת��
		else if (keyCode == KeyEvent.VK_UP && !direction.equals("D")) {
			direction = "U";
		} else if (keyCode == KeyEvent.VK_DOWN && !direction.equals("U")) {
			direction = "D";
		} else if (keyCode == KeyEvent.VK_LEFT && !direction.equals("R")) {
			direction = "L";
		} else if (keyCode == KeyEvent.VK_RIGHT && !direction.equals("L")) {
			direction = "R";
		}

		// 处理弹窗按键
		if (showLevelUpPopup || showGameOverPopup) {
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
				if (showLevelUpPopup) {
					nextLevel();
					// 删除存档文件
					deleteSaveFile();
				} else if (showGameOverPopup) {
					initSnake();
					// 删除存档文件
					deleteSaveFile();
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * 1.��������
	 * 2.���ƶ�
	 * 3.�ػ�һ����
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		timer.start();

		if (isStarted && !isFaild && !showLevelUpPopup && !showGameOverPopup) {
			// 倒计时
			countdown--;

			// 检查倒计时是否结束
			if (countdown <= 0) {
				showGameOverPopup = true;
				isStarted = false;
			}

			// �ƶ�����
			for (int i = len; i > 0; i--) {
				snakex[i] = snakex[i - 1];
				snakey[i] = snakey[i - 1];
			}
			// ͷ�ƶ�
			if (direction.equals("R")) {
				// ������+25
				snakex[0] = snakex[0] + 25;
				if (snakex[0] > 850)
					snakex[0] = 25;

			} else if (direction.equals("L")) {
				// ������-25
				snakex[0] = snakex[0] - 25;
				if (snakex[0] < 25)
					snakex[0] = 850;
			} else if (direction.equals("U")) {
				// ������-25
				snakey[0] = snakey[0] - 25;
				if (snakey[0] < 75)
					snakey[0] = 650;
			} else if (direction.equals("D")) {
				// ������+25
				snakey[0] = snakey[0] + 25;
				if (snakey[0] > 650)
					snakey[0] = 75;
			}
			// ��ʳ��
			if (snakex[0] == foodx && snakey[0] == foody) {
				len++;
				eatenFood++;
				score += 10;
				countdown += 2; // 每吃一个食物增加2秒倒计时
				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// 检查是否完成当前关卡
				if (eatenFood >= targetFood) {
					showLevelUpPopup = true;
					isStarted = false;
				}
			}
			// �ж���Ϸʧ��
			for (int i = 1; i < len; i++) {
				if (snakex[0] == snakex[i] && snakey[0] == snakey[i]) {
					showGameOverPopup = true;
					isStarted = false;
				}
			}
		}
		repaint();
	}

	// 进入下一关
	public void nextLevel() {
		level++;
		targetFood += 3; // 每关食物目标数+3
		eatenFood = 0;
		countdown = 30 + (level - 1) * 5; // 初始30秒，每升1级增加5秒
		showLevelUpPopup = false;
		isStarted = true;

		// 提升蛇的移动速度（减少计时器延迟）
		if (timer.getDelay() > 50) { // 最小延迟为50毫秒
			timer.setDelay(timer.getDelay() - 10);
		}
	}

	// 检查是否有存档文件
	private boolean hasSaveFile() {
		File saveFile = new File(SAVE_FILE_PATH);
		return saveFile.exists() && saveFile.isFile();
	}

	// 保存游戏状态
	private void saveGame() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE_PATH))) {
			// 保存关卡信息
			writer.write(level + "\n");
			writer.write(score + "\n");
			writer.write(targetFood + "\n");
			writer.write(eatenFood + "\n");
			writer.write(countdown + "\n");
			writer.write(timer.getDelay() + "\n");

			// 保存蛇的状态
			writer.write(len + "\n");
			writer.write(direction + "\n");
			for (int i = 0; i < len; i++) {
				writer.write(snakex[i] + "," + snakey[i] + "\n");
			}

			// 保存食物位置
			writer.write(foodx + "\n");
			writer.write(foody + "\n");

			// 保存游戏状态
			writer.write(isStarted + "\n");
			writer.write(isFaild + "\n");
			writer.write(showLevelUpPopup + "\n");
			writer.write(showGameOverPopup + "\n");

			System.out.println("游戏已保存");
		} catch (IOException e) {
			System.err.println("保存游戏失败: " + e.getMessage());
		}
	}

	// 加载游戏状态
	private void loadGame() {
		try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE_PATH))) {
			// 加载关卡信息
			level = Integer.parseInt(reader.readLine());
			score = Integer.parseInt(reader.readLine());
			targetFood = Integer.parseInt(reader.readLine());
			eatenFood = Integer.parseInt(reader.readLine());
			countdown = Integer.parseInt(reader.readLine());
			int delay = Integer.parseInt(reader.readLine());
			timer.setDelay(delay);

			// 加载蛇的状态
			len = Integer.parseInt(reader.readLine());
			direction = reader.readLine();
			for (int i = 0; i < len; i++) {
				String[] coordinates = reader.readLine().split(",");
				snakex[i] = Integer.parseInt(coordinates[0]);
				snakey[i] = Integer.parseInt(coordinates[1]);
			}

			// 加载食物位置
			foodx = Integer.parseInt(reader.readLine());
			foody = Integer.parseInt(reader.readLine());

			// 加载游戏状态
			isStarted = Boolean.parseBoolean(reader.readLine());
			isFaild = Boolean.parseBoolean(reader.readLine());
			showLevelUpPopup = Boolean.parseBoolean(reader.readLine());
			showGameOverPopup = Boolean.parseBoolean(reader.readLine());

			System.out.println("游戏已加载");
		} catch (IOException | NumberFormatException e) {
			System.err.println("加载游戏失败: " + e.getMessage());
			// 加载失败时初始化游戏
			initSnake();
		}
	}

	// 删除存档文件
	private void deleteSaveFile() {
		File saveFile = new File(SAVE_FILE_PATH);
		if (saveFile.exists()) {
			if (saveFile.delete()) {
				System.out.println("存档已删除");
			} else {
				System.err.println("删除存档失败");
			}
		}
	}
}
