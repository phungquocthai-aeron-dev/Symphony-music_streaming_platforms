import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';

// models/fruit.model.ts
interface Fruit {
  id: number;
  name: string;
  image: string;
  type: 'fruit' | 'bomb';
}

// models/task.model.ts
interface Task {
  id: number;
  timeLimit: number; // in seconds
  targets: { fruitName: string; count: number }[];
  rewardCoins: number;
}

const FRUITS: Fruit[] = [
  { id: 1, name: 'watermelon', image: 'https://icons.iconarchive.com/icons/iconarchive/flat-fruit-soft/48/Watermelon-Melon-Flat-icon.png', type: 'fruit' },
  { id: 2, name: 'orange', image: 'https://icons.iconarchive.com/icons/iconarchive/flat-fruit-soft/72/Orange-Flat-icon.png', type: 'fruit' },
  { id: 3, name: 'apple', image: 'https://icons.iconarchive.com/icons/iconarchive/flat-fruit-soft/72/Apple-Red-Flat-icon.png', type: 'fruit' },
  { id: 4, name: 'bomb', image: 'https://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/128/Actions-edit-bomb-icon.png', type: 'bomb' }
];

const DAILY_TASK: Task = {
  id: 1,
  timeLimit: 90,
  targets: [
    { fruitName: 'watermelon', count: 3 },
    { fruitName: 'orange', count: 2 }
  ],
  rewardCoins: 100
};


@Component({
  selector: 'app-games',
  imports: [ NgFor ],
  templateUrl: './games.component.html',
  styleUrl: './games.component.css'
})
export class GamesComponent implements OnInit {
  hearts: number = 3;
  coins: number = 0;
  task: Task = DAILY_TASK;
  taskProgress: { [fruit: string]: number } = {};
  fruitsOnScreen: { fruit: Fruit; x: number; y: number; id: number }[] = [];
  intervalId: any;
  timeLeft: number = this.task.timeLimit;
  isPlaying = false;

  ngOnInit(): void {
    this.resetTaskProgress();
  }

  resetTaskProgress() {
    this.taskProgress = {};
    for (const target of this.task.targets) {
      this.taskProgress[target.fruitName] = 0;
    }
  }

  startGame() {
    this.isPlaying = true;
    this.timeLeft = this.task.timeLimit;
    this.fruitsOnScreen = [];
    this.resetTaskProgress();
    this.intervalId = setInterval(() => this.spawnFruit(), 1000);
    const timer = setInterval(() => {
      this.timeLeft--;
      if (this.timeLeft <= 0) {
        clearInterval(timer);
        clearInterval(this.intervalId);
        this.endGame();
      }
    }, 1000);
  }

  spawnFruit() {
    const fruit = FRUITS[Math.floor(Math.random() * FRUITS.length)];
    const id = new Date().getTime();
    const x = Math.floor(Math.random() * 300);
    const y = Math.floor(Math.random() * 300);
    this.fruitsOnScreen.push({ fruit, x, y, id });

    setTimeout(() => {
      this.fruitsOnScreen = this.fruitsOnScreen.filter(f => f.id !== id);
    }, 3000);
  }

  sliceFruit(fruitId: number) {
    const sliced = this.fruitsOnScreen.find(f => f.id === fruitId);
    if (!sliced) return;

    if (sliced.fruit.type === 'bomb') {
      this.hearts--;
    } else {
      if (this.taskProgress[sliced.fruit.name] !== undefined) {
        this.taskProgress[sliced.fruit.name]++;
      }
    }

    this.fruitsOnScreen = this.fruitsOnScreen.filter(f => f.id !== fruitId);

    if (this.checkTaskComplete()) {
      this.coins += this.task.rewardCoins;
      clearInterval(this.intervalId);
      this.isPlaying = false;
      alert('🎉 Hoàn thành nhiệm vụ!');
    }

    if (this.hearts <= 0) {
      this.endGame();
    }
  }

  checkTaskComplete(): boolean {
    return this.task.targets.every(target =>
      this.taskProgress[target.fruitName] >= target.count
    );
  }

  endGame() {
    this.isPlaying = false;
    clearInterval(this.intervalId);
    alert('😢 Game over! Bạn đã hết tim hoặc không hoàn thành nhiệm vụ.');
  }
}
