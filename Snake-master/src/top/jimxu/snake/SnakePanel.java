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
	int eatenFood = 0; // 当前关卡已吃食物数量
	int countdown = 30; // 关卡倒计时（秒）
	int score = 0; // 得分
	boolean isLevelComplete = false; // 关卡是否完成

	// 存档文件路径
	private static final String SAVE_FILE_PATH = "snake_save.dat";

	// ��ʼ����
	public void initSnake() {
		isStarted = false;
		isFaild = false;
		isLevelComplete = false;
		len = 3;
		direction = "R";
		snakex[0] = 100;
		snakey[0] = 100;
		snakex[1] = 75;
		snakey[1] = 100;
		snakex[2] = 50;
		snakey[2] = 100;
		// 重置关卡和得分
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
		// 检查是否有存档并询问是否恢复
		if (hasSaveFile() && showLoadConfirmation()) {
			loadGame();
		} else {
			initSnake(); // ���þ�̬�ߣ�
		}
		this.addKeyListener(this);// ���ӿ�
		timer.start();
		countdownTimer.start();
	}

	// �������ƶ��ٶ�
	Timer timer = new Timer(150, this);
	// 倒计时计时器
	Timer countdownTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (isStarted && !isFaild && !isLevelComplete && countdown > 0) {
				countdown--;
				if (countdown <= 0) {
					isFaild = true;
				}
			}
		}
	});

	public void paint(Graphics g) {
		// ���ñ�����ɫ
		this.setBackground(Color.black);
		g.fillRect(25, 75, 850, 600);
		// ���ñ���
		title.paintIcon(this, g, 25, 11);

		// 绘制关卡信息面板
		g.setColor(Color.GRAY);
		g.fillRect(650, 11, 225, 60);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 14));
		g.drawString("Level: " + level, 660, 30);
		g.drawString("Food: " + eatenFood + "/" + targetFood, 660, 48);
		g.drawString("Time: " + countdown + "s", 660, 66);
		g.drawString("Score: " + score, 780, 30);

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
			// 绘制失败弹窗
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(25, 75, 850, 600);
			g.setColor(Color.RED);
			g.fillRect(300, 250, 300, 150);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Game Over!", 400, 300);
			g.drawString("Final Score: " + score, 380, 330);
			g.drawString("Press Space to Retry", 360, 360);
		}
		// 关卡完成弹窗
		if (isLevelComplete) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(25, 75, 850, 600);
			g.setColor(Color.GREEN);
			g.fillRect(300, 250, 300, 150);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString("Level " + level + " Complete!", 360, 300);
			g.drawString("Advance to Level " + (level + 1), 350, 330);
			g.drawString("Press Space to Continue", 340, 360);
		}

		// ��ʳ��
		food.paintIcon(this, g, foodx, foody);

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
				deleteSaveFile(); // 游戏失败后删除存档
			} else if (isLevelComplete) {
				// 进入下一关
				nextLevel();
				deleteSaveFile(); // 通关后删除存档
			} else {
				isStarted = !isStarted;
				if (!isStarted) {
					// 暂停时自动存档
					saveGame();
				}
			}
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
		timer.start();

		if (isStarted && !isFaild && !isLevelComplete) {
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
				countdown += 2; // 每吃一个食物增加2秒
				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// 检查是否完成关卡
				if (eatenFood >= targetFood) {
					isLevelComplete = true;
				}
			}
			// �ж���Ϸʧ��
			for (int i = 1; i < len; i++) {
				if (snakex[0] == snakex[i] && snakey[0] == snakey[i]) {
					isFaild = true;
				}
			}
		}
		repaint();
	}

	// 进入下一关
	private void nextLevel() {
		level++;
		targetFood += 3; // 每关增加3个食物目标
		eatenFood = 0;
		countdown = 30 + (level - 1) * 5; // 每升一级增加5秒
		isLevelComplete = false;
		isStarted = true;

		// 提升蛇的移动速度（最多减少到50毫秒）
		int newDelay = Math.max(50, 150 - (level - 1) * 10);
		timer.setDelay(newDelay);

		// 重置食物位置
		foodx = r.nextInt(34) * 25 + 25;
		foody = r.nextInt(24) * 25 + 75;
	}

	// 检查是否存在存档文件
	private boolean hasSaveFile() {
		File saveFile = new File(SAVE_FILE_PATH);
		return saveFile.exists() && saveFile.length() > 0;
	}

	// 显示是否恢复存档的确认对话框
	private boolean showLoadConfirmation() {
		int option = JOptionPane.showConfirmDialog(
				this,
				"检测到存档文件，是否恢复上次游戏？",
				"恢复游戏",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return option == JOptionPane.YES_OPTION;
	}

	// 保存游戏状态到本地文件
	private void saveGame() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE_PATH))) {
			// 保存基本游戏状态
			writer.write(level + "\n");
			writer.write(score + "\n");
			writer.write(len + "\n");
			writer.write(direction + "\n");
			writer.write(countdown + "\n");
			writer.write(eatenFood + "\n");
			writer.write(targetFood + "\n");
			writer.write(timer.getDelay() + "\n");
			writer.write(foodx + "\n");
			writer.write(foody + "\n");

			// 保存蛇身坐标
			for (int i = 0; i < len; i++) {
				writer.write(snakex[i] + "," + snakey[i] + "\n");
			}

			System.out.println("游戏已存档");
		} catch (IOException e) {
			System.err.println("存档失败: " + e.getMessage());
		}
	}

	// 从本地文件加载游戏状态
	private void loadGame() {
		try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE_PATH))) {
			// 加载基本游戏状态
			level = Integer.parseInt(reader.readLine());
			score = Integer.parseInt(reader.readLine());
			len = Integer.parseInt(reader.readLine());
			direction = reader.readLine();
			countdown = Integer.parseInt(reader.readLine());
			eatenFood = Integer.parseInt(reader.readLine());
			targetFood = Integer.parseInt(reader.readLine());
			int delay = Integer.parseInt(reader.readLine());
			foodx = Integer.parseInt(reader.readLine());
			foody = Integer.parseInt(reader.readLine());

			// 加载蛇身坐标
			for (int i = 0; i < len; i++) {
				String[] coordinates = reader.readLine().split(",");
				snakex[i] = Integer.parseInt(coordinates[0]);
				snakey[i] = Integer.parseInt(coordinates[1]);
			}

			// 设置计时器延迟
			timer.setDelay(delay);

			// 设置游戏状态
			isStarted = false; // 加载后处于暂停状态
			isFaild = false;
			isLevelComplete = false;

			System.out.println("游戏已恢复");
		} catch (IOException | NumberFormatException e) {
			System.err.println("读档失败: " + e.getMessage());
			// 读档失败时初始化新游戏
			initSnake();
		}
	}

	// 删除存档文件
	private void deleteSaveFile() {
		File saveFile = new File(SAVE_FILE_PATH);
		if (saveFile.exists() && saveFile.delete()) {
			System.out.println("存档已删除");
		}
	}
}
