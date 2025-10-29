# Space Invaders Game

Game Space Invaders được phát triển bằng Java Swing.

## Cấu trúc dự án

```
SpaceInvaders/
├── src/main/
│   ├── Game.java              # Khởi tạo JFrame, game loop
│   ├── GamePanel.java         # JPanel, vẽ mọi thứ
│   ├── Player.java            # Tàu người chơi
│   ├── Enemy.java             # Kẻ địch
│   ├── Bullet.java            # Đạn bắn
│   ├── InputHandler.java      # Xử lý phím
│   ├── Collision.java         # Logic va chạm
│   ├── GameState.java         # Enum {MENU, PLAYING, GAMEOVER}
│   └── Assets.java            # Load hình, âm thanh
├── resources/
│   ├── images/                # Hình ảnh game
│   └── sounds/                # Âm thanh game
└── README.md
```

## Cách chạy

1. Compile và chạy file `Game.java`:

   ```bash
   javac src/main/*.java
   java -cp src/main Game
   ```

2. Hoặc sử dụng IDE như IntelliJ IDEA, Eclipse, VScode để chạy

## Điều khiển

- **Mũi tên trái/phải**: Di chuyển tàu
- **Space**: Bắn đạn
- **P**: Tạm dừng/Tiếp tục
- **Enter**: Bắt đầu game/Restart
- **Escape**: Thoát game

## Phân chia công việc

- **Thành viên 1**: Game framework, game loop, tích hợp các lớp
- **Thành viên 2**: Player class (di chuyển, bắn đạn)
- **Thành viên 3**: Enemy class và quản lý danh sách quái
- **Thành viên 4**: ScoreBoard/GameUI, âm thanh, giao diện

## Tính năng

- [x] Game loop 60 FPS
- [x] Xử lý input bàn phím
- [x] Hệ thống va chạm
- [x] Quản lý trạng thái game
- [x] Load tài nguyên (hình ảnh, âm thanh)
- [x] UI cơ bản (điểm số, mạng sống)
- [ ] Player movement và shooting (Thành viên 2)
- [ ] Enemy AI và spawning (Thành viên 3)
- [ ] ScoreBoard và âm thanh (Thành viên 4)
