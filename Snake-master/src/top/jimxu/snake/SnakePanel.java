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

	// 关卡相关变量
	int level = 1; // 当前关卡
	int foodTarget = 5; // 当前关卡需要吃的食物数量
	int foodEaten = 0; // 已经吃的食物数量
	int score = 0; // 得分
	int countdown = 30; // 关卡倒计时（秒）
	Timer countdownTimer; // 倒计时计时器
	boolean isLevelCompleted = false; // 是否完成当前关卡

	// ��ʼ����
	public void initSnake() {
		isStarted = false;
		isFaild = false;
		isLevelCompleted = false;
		len = 3;
		direction = "R";
		snakex[0] = 100;
		snakey[0] = 100;
		snakex[1] = 75;
		snakey[1] = 100;
		snakex[2] = 50;
		snakey[2] = 100;

		// 重置关卡相关变量
		level = 1;
		foodTarget = 5;
		foodEaten = 0;
		score = 0;
		countdown = 30;
		// 重置速度
		timer.setDelay(150);
	}

	public SnakePanel() {
		// 检查是否有存档
		if (hasSaveFile()) {
			int option = JOptionPane.showConfirmDialog(null, "是否恢复上次存档？", "恢复存档", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				loadGame();
			} else {
				initSnake();
			}
		} else {
			initSnake();
		}
		this.setFocusable(true);
		this.addKeyListener(this);// ���Ӽ��̼����ӿ�
		timer.start();

		// 初始化倒计时计时器
		countdownTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isStarted && !isFaild && !isLevelCompleted && countdown > 0) {
					countdown--;
					if (countdown == 0) {
						isFaild = true;
					}
				}
			}
		});
		countdownTimer.start();
	}

	// 检查是否存在存档文件
	private boolean hasSaveFile() {
		java.io.File saveFile = new java.io.File("snake_save.txt");
		return saveFile.exists() && saveFile.isFile();
	}

	// 加载游戏存档
	private void loadGame() {
		try (BufferedReader reader = new BufferedReader(new FileReader("snake_save.txt"))) {
			// 读取关卡信息
			level = Integer.parseInt(reader.readLine());
			score = Integer.parseInt(reader.readLine());
			countdown = Integer.parseInt(reader.readLine());
			foodTarget = Integer.parseInt(reader.readLine());
			foodEaten = Integer.parseInt(reader.readLine());
			direction = reader.readLine();
			len = Integer.parseInt(reader.readLine());

			// 读取食物位置
			foodx = Integer.parseInt(reader.readLine());
			foody = Integer.parseInt(reader.readLine());

			// 读取蛇身坐标
			for (int i = 0; i < len; i++) {
				String[] parts = reader.readLine().split(" ");
				snakex[i] = Integer.parseInt(parts[0]);
				snakey[i] = Integer.parseInt(parts[1]);
			}

			// 设置游戏状态
			isStarted = false;
			isFaild = false;
			isLevelCompleted = false;

			// 调整速度
			int newDelay = Math.max(50, 150 - (level - 1) * 10);
			timer.setDelay(newDelay);

			System.out.println("游戏已加载");
		} catch (IOException | NumberFormatException e) {
			System.out.println("加载游戏失败: " + e.getMessage());
			initSnake(); // 如果加载失败，初始化新游戏
		}
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
		g.setColor(new Color(0, 150, 0));
		g.fillRect(650, 25, 225, 120);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.drawString("关卡信息", 720, 55);
		g.setFont(new Font("arial", Font.PLAIN, 16));
		g.drawString("当前关卡: " + level, 670, 85);
		g.drawString("剩余食物: " + (foodTarget - foodEaten), 670, 110);
		g.drawString("倒计时: " + countdown + "s", 670, 135);
		g.drawString("得分: " + score, 670, 160);

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
			g.drawString("Game Over! 得分: " + score + " 按空格重新开始", 180, 350);
		}

		// 绘制关卡完成弹窗
		if (isLevelCompleted) {
			g.setColor(new Color(0, 0, 0, 180));
			g.fillRect(25, 75, 850, 600);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.drawString("进阶至第" + (level + 1) + "关!", 300, 300);
			g.setFont(new Font("arial", Font.PLAIN, 24));
			g.drawString("按空格键进入下一关", 320, 350);
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
			} else if (isLevelCompleted) {
				// 进入下一关
				level++;
				foodTarget = 5 + (level - 1) * 3;
				foodEaten = 0;
				countdown = 30 + (level - 1) * 5;
				isLevelCompleted = false;
				// 提升速度
				int newDelay = Math.max(50, 150 - (level - 1) * 10);
				timer.setDelay(newDelay);
			} else {
				isStarted = !isStarted;
				// 如果是暂停游戏，则保存存档
				if (!isStarted) {
					saveGame();
				}
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

	}

	// 保存游戏存档
	private void saveGame() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("snake_save.txt"))) {
			// 保存关卡信息
			writer.write(level + "");
			writer.newLine();
			writer.write(score + "");
			writer.newLine();
			writer.write(countdown + "");
			writer.newLine();
			writer.write(foodTarget + "");
			writer.newLine();
			writer.write(foodEaten + "");
			writer.newLine();
			writer.write(direction + "");
			writer.newLine();
			writer.write(len + "");
			writer.newLine();

			// 保存食物位置
			writer.write(foodx + "");
			writer.newLine();
			writer.write(foody + "");
			writer.newLine();

			// 保存蛇身坐标
			for (int i = 0; i < len; i++) {
				writer.write(snakex[i] + " " + snakey[i]);
				writer.newLine();
			}

			System.out.println("游戏已保存");
		} catch (IOException e) {
			System.out.println("保存游戏失败: " + e.getMessage());
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

		if (isStarted && !isFaild) {
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
				foodEaten++;
				score += 10;
				countdown += 2; // 每吃一个食物加2秒

				foodx = r.nextInt(34) * 25 + 25;
				foody = r.nextInt(24) * 25 + 75;

				// 检查是否完成关卡
				if (foodEaten >= foodTarget) {
					isLevelCompleted = true;
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
}
