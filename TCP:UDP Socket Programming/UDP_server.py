import socket

# 통신 정보 설정
IP = ""
PORT = 4001
SIZE = 1024
ADDR = (IP, PORT)

# 서버 소켓 설정
server_socket =  socket.socket(socket.AF_INET, socket.SOCK_DGRAM) 
    # AF_INET : IPv4의 네트워크 주소 체계
    # SOCK_STREAM : TCP protocol로 통신하는 경우
    # SOCK_DGRAM : UDP protocol로 통신하는 경우 
server_socket.bind(ADDR)  
    # 주소 바인딩
    # bind는 서버측 포트에 연결하겠다는 의미로 튜플로 정의된 (IP, PORT)를 바인딩 함
    # bind는 소켓과 AF를 연결하는 과정이기 때문에 튜플로 정의해야함

while 1:
    client_data, client_addr = server_socket.recvfrom(2048)  
    # 수신 대기 중, 접속한 클라이언트의 소켓과 주소를 반환
    # UDP의 경우 listen, connect, accept의 과정이 따로 필요하지 않음
    # 따로 수신자를 확인하는 과정 없이 sendto() recvfrom()에 도착지의 주소와 데이터를 함께 실어 전송
    print("CLIENT >>"+str(client_data.decode()))
    server_socket.sendto(client_data, client_addr)
