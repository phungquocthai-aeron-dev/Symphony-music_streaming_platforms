export interface SongCreateDTO {
    songName: string;
    total_listens?: number; // Giá trị mặc định là 0
    duration: number;
    releaseDate: string; // Trong Angular, LocalDate được biểu diễn dưới dạng string (ISO format)
    author: string;
    isVip: boolean;
    categoryIds: number[]; // Set<Integer> → number[]
    singersId: number[]; // Set<Integer> → number[]
  }
  