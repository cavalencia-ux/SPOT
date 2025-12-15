#!/usr/bin/env python3
"""
Simple HTTP server for SPOT Parking Management System
Serves static files on http://localhost:8080
"""

import http.server
import socketserver
import os
from pathlib import Path

PORT = 8080
DIRECTORY = Path(__file__).parent / "src" / "main" / "resources" / "static"

class MyHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(DIRECTORY), **kwargs)
    
    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

if __name__ == "__main__":
    os.chdir(DIRECTORY)
    
    with socketserver.TCPServer(("", PORT), MyHTTPRequestHandler) as httpd:
        print(f"✅ SPOT Parking System is running!")
        print(f"📱 Open your browser: http://localhost:{PORT}")
        print(f"📁 Serving files from: {DIRECTORY}")
        print(f"⏹️  Press Ctrl+C to stop the server")
        httpd.serve_forever()
