import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response } from 'express';
import axios from 'axios';

interface RouteConfig {
  path: string;
  target: string;
}

@Injectable()
export class ProxyMiddleware implements NestMiddleware {
  private routes: RouteConfig[] = [
    { path: '/api/admin', target: 'http://localhost:8002' },
    { path: '/api/customer', target: 'http://localhost:8002' },
    { path: '/api/user', target: 'http://localhost:8001' },
    { path: '/api/system', target: 'http://localhost:8001' },
    { path: '/api/role', target: 'http://localhost:8001' },
    { path: '/api/menu', target: 'http://localhost:8001' },
    { path: '/api/permission', target: 'http://localhost:8001' },
    { path: '/api/product', target: 'http://localhost:8001' },
    { path: '/api/customers', target: 'http://localhost:8001' },
    { path: '/api/address', target: 'http://localhost:8001' },
    { path: '/api/home', target: 'http://localhost:8001' },
    { path: '/api/admin/banner', target: 'http://localhost:8001' },
    { path: '/api/order', target: 'http://localhost:8006' },
    { path: '/api/stock', target: 'http://localhost:8007' },
    { path: '/api/upload', target: 'http://localhost:8003' },
    { path: '/api/cart', target: 'http://localhost:8004' },
    { path: '/api/pay', target: 'http://localhost:8005' },
    { path: '/files', target: 'http://localhost:8003' },
  ];

  use(req: Request, res: Response, next: Function) {
    const path = req.path;
    
    // 查找匹配的路由
    const route = this.routes.find(r => path.startsWith(r.path));
    
    if (route) {
      const targetUrl = `${route.target}${path}`;
      
      // 转发请求
      axios({
        method: req.method,
        url: targetUrl,
        data: req.body,
        headers: { ...req.headers, host: undefined },
        params: req.query,
        responseType: 'arraybuffer',
      }).then(response => {
        res.status(response.status).set(response.headers).send(response.data);
      }).catch(error => {
        if (error.response) {
          res.status(error.response.status).set(error.response.headers).send(error.response.data);
        } else {
          res.status(500).json({ code: 500, message: 'Gateway Error', data: null });
        }
      });
    } else {
      next();
    }
  }
}
