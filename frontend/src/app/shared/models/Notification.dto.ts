export interface NotificationDTO {
  notificationId: number;
  isRead: boolean;
  message: string;
  type: string;
  createdAt: string;
  senderId: number;
  recipientIds: number[];
}