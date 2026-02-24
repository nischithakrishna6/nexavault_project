import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import SockJS from 'sockjs-client';


@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private client: Client | null = null;
  private notificationSubject = new Subject<string>();
  public notifications$ = this.notificationSubject.asObservable();

  connect(userId: number): void {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws') as any,
      debug: (str: string) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log('WebSocket Connected');
      this.client?.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        this.notificationSubject.next(message.body);
      });
    };

    this.client.onStompError = (frame: any) => {
      console.error('WebSocket Error:', frame);
    };

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }
}
