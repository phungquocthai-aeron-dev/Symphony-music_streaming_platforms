import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';

@Injectable({ providedIn: 'root' })
export class VNPayService {
  private apiUrl = 'http://localhost:8080/api/payment';

  constructor(private http: HttpClient) {}

  /**
   * Tạo URL thanh toán VNPay
   * @param amount Số tiền (VND)
   * @param language Ngôn ngữ ("vn" hoặc "en")
   * @param bankCode Mã ngân hàng (tùy chọn)
   * @param userId ID người dùng (dùng để lưu vào hóa đơn)
   */
  createPayment(
    amount: number,
    userId: number,
    language?: string,
    bankCode?: string
  ): Observable<ResponseData<string>> {
    // Sử dụng params thay vì body
    let params = new HttpParams()
      .set('amount', amount.toString())
      .set('userId', userId.toString());
  
    // Thêm các tham số tùy chọn nếu có
    if (language) params = params.set('language', language);
    if (bankCode) params = params.set('bankCode', bankCode);
  
    return this.http.post<ResponseData<string>>(
      `${this.apiUrl}/create-payment`,
      null, 
      { params }
    );
  }
  

  /**
   * Gọi khi VNPay redirect về để xác minh trạng thái thanh toán
   * @param params Các tham số VNPay trả về (vnp_ResponseCode, vnp_OrderInfo, ...)
   */
  handleVnpayReturn(params: { [key: string]: string }): Observable<any> {
    let httpParams = new HttpParams();
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        httpParams = httpParams.set(key, params[key]);
      }
    }

    return this.http.get(`${this.apiUrl}/ipn`, { params: httpParams });
  }
}
